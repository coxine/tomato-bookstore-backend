package tg.cos.tomatomall.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tg.cos.tomatomall.po.*;
import tg.cos.tomatomall.repository.AccountRepository;
import tg.cos.tomatomall.repository.OrderRepository;
import tg.cos.tomatomall.repository.StockpileRepository;
import tg.cos.tomatomall.service.OrderService;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.ChapterGetAllVO;
import tg.cos.tomatomall.vo.OrderPayVO;
import tg.cos.tomatomall.vo.OrderFormsVO;
import tg.cos.tomatomall.vo.OrderItemFormVO;


import com.alipay.api.*;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.*;
import com.alipay.api.request.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Value("${alipay.app-id}")
    private String APPID;

    @Value("${alipay.private-key}")
    private String PRIVATEKEY;
    @Value("${alipay.alipay-public-key}")
    private String ALIPAYPUBLICKEY;
    @Value("${alipay.server-url}")
    private String SERVERURL;
    @Value("${alipay.charset}")
    private String CHARSET;
    @Value("${alipay.sign-type}")
    private String SIGNTYPE;
    @Value("${alipay.notify-url}")
    private String NOTIFYURL;
    @Value("${alipay.return-url}")
    private String RETURNURL;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public OrderPayVO pay(Integer orderId){
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if(orderOptional.isEmpty()){
            return null;
        }
        Order order = orderOptional.get();
        if (!"PENDING".equals(order.getStatus())) {
            return null;
        }

        try {
            //  创建支付宝客户端
            AlipayClient alipayClient = new DefaultAlipayClient(
                    SERVERURL,
                    APPID,
                    PRIVATEKEY,
                    "json",
                    CHARSET,
                    ALIPAYPUBLICKEY,
                    SIGNTYPE);

            //  创建支付请求
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setReturnUrl(RETURNURL);
            request.setNotifyUrl(NOTIFYURL);

            //  构建业务参数
            JSONObject bizContent = new JSONObject();
            bizContent.put("out_trade_no", orderId);
            bizContent.put("total_amount", order.getTotalAmount());
            bizContent.put("subject", "payment for order " + orderId);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

            request.setBizContent(bizContent.toString());

            //  生成支付表单
            String form = alipayClient.pageExecute(request).getBody();
            // System.err.println(form);
            OrderPayVO res = new OrderPayVO();
            res.setPaymentForm(form);
            res.setOrderId(orderId);
            res.setTotalAmount(order.getTotalAmount());
            res.setPaymentMethod(order.getPaymentMethod());
            return res;
        } catch (Exception e) {
            return null;
        }


    }

    @Override
    public void updateOrderStatus(String orderId, String alipayTradeNo, String amount,String status){
        Integer id = Integer.parseInt(orderId);
        Optional<Order> orderOptional = orderRepository.findById(id);
        if(orderOptional.isEmpty()){
            return;
        }
        Order order = orderOptional.get();
        order.setAlipayTradeNo(alipayTradeNo);
        BigDecimal payAmount = new BigDecimal(amount);
        order.setPayAmount(payAmount);
        order.setStatus(status);
        orderRepository.save(order);
        Account account = order.getAccount();

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            Stockpile stockpile = product.getStockpile();
            stockpile.setFrozen(stockpile.getFrozen() - item.getQuantity());
            if (!item.getChapters().isEmpty()){
                for (Chapter chapter : item.getChapters()) {
                    account.getChapters().add(chapter);

                }
                accountRepository.save(account);
            }
        }
    }

    @Override
    public List<OrderFormsVO> getUserOrders(Integer accountId) {
        Account account = securityUtil.getCurrentUser();
        if ( account.getId() != accountId) {
            return null;
        }
        List<Order> orders = orderRepository.findByAccountId(accountId);
        return orders.stream().map(order -> {
            OrderFormsVO vo = new OrderFormsVO();
            vo.setOrderId(order.getId());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setPaymentMethod(order.getPaymentMethod());
            vo.setStatus(order.getStatus());
            vo.setCreateTime(order.getCreateTime());
            vo.setName(order.getName());
            vo.setAddress(order.getAddress());
            vo.setPhone(order.getPhone());
            
            // 转换订单项
            List<OrderItemFormVO> orderItems = order.getOrderItems().stream().map(item -> {
                OrderItemFormVO itemVO = new OrderItemFormVO();
                itemVO.setProductId(item.getProduct().getId());
                itemVO.setProductTitle(item.getProduct().getTitle());
                itemVO.setQuantity(item.getQuantity());
                itemVO.setPrice(item.getProduct().getPrice());
                itemVO.setCover(item.getProduct().getCover());

                if (!item.getChapters().isEmpty()) {
                    itemVO.setFullyPurchased(false);
                    List<ChapterGetAllVO> chapters = item.getChapters().stream().map(chapter -> {
                        ChapterGetAllVO chapterVO = new ChapterGetAllVO();
                        chapterVO.setProductId(chapter.getProduct().getId());
                        chapterVO.setStatus(chapter.getStatus());
                        chapterVO.setId(chapter.getId());
                        Integer next = chapter.getNext();
                        Integer previous = chapter.getPrevious();
                        chapterVO.setNext(next != null ? next : 0);
                        chapterVO.setName(chapter.getName());
                        chapterVO.setPrevious(previous != null ? previous : 0);
                        return chapterVO;
                    }).toList();
                    itemVO.setChapters(chapters);
                }else {
                    itemVO.setFullyPurchased(true);
                    itemVO.setChapters(new ArrayList<>());
                }

                return itemVO;
            }).collect(Collectors.toList());
            
            vo.setOrderItems(orderItems);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OrderFormsVO> getAllOrders() {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().equalsIgnoreCase("admin")) {
            return null;
        }
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> {
            OrderFormsVO vo = new OrderFormsVO();
            vo.setOrderId(order.getId());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setPaymentMethod(order.getPaymentMethod());
            vo.setStatus(order.getStatus());
            vo.setCreateTime(order.getCreateTime());
            vo.setName(order.getName());
            vo.setAddress(order.getAddress());
            vo.setPhone(order.getPhone());
            
            // 转换订单项
            List<OrderItemFormVO> orderItems = order.getOrderItems().stream().map(item -> {
                OrderItemFormVO itemVO = new OrderItemFormVO();
                itemVO.setProductId(item.getProduct().getId());
                itemVO.setProductTitle(item.getProduct().getTitle());
                itemVO.setQuantity(item.getQuantity());
                itemVO.setPrice(item.getProduct().getPrice());
                itemVO.setCover(item.getProduct().getCover());
                if (!item.getChapters().isEmpty()) {
                    itemVO.setFullyPurchased(false);
                    List<ChapterGetAllVO> chapters = item.getChapters().stream().map(chapter -> {
                        ChapterGetAllVO chapterVO = new ChapterGetAllVO();
                        chapterVO.setProductId(chapter.getProduct().getId());
                        chapterVO.setStatus(chapter.getStatus());
                        chapterVO.setId(chapter.getId());
                        Integer next = chapter.getNext();
                        Integer previous = chapter.getPrevious();
                        chapterVO.setNext(next != null ? next : 0);
                        chapterVO.setName(chapter.getName());
                        chapterVO.setPrevious(previous != null ? previous : 0);
                        return chapterVO;
                    }).toList();
                    itemVO.setChapters(chapters);
                }else {
                    itemVO.setFullyPurchased(true);
                    itemVO.setChapters(new ArrayList<>());
                }
                return itemVO;
            }).collect(Collectors.toList());
            
            vo.setOrderItems(orderItems);
            return vo;
        }).collect(Collectors.toList());
    }

    // 每5分钟执行一次
    @Scheduled(fixedRate = 300000)
    public void checkAndReleaseTimeoutOrders() {
        System.out.println("checkAndReleaseTimeoutOrders");
        // 获取30分钟前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        Date thirtyMinutesAgo = calendar.getTime();
        
        // 查找所有超时未支付的订单
        List<Order> timeoutOrders = orderRepository.findByStatusAndCreateTimeBefore("PENDING", thirtyMinutesAgo);
        
        for (Order order : timeoutOrders) {
            // 释放库存
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                Stockpile stockpile = product.getStockpile();
                
                // 减少冻结数量
                stockpile.setFrozen(stockpile.getFrozen() - orderItem.getQuantity());
                stockpile.setAmount(stockpile.getAmount() + orderItem.getQuantity());
                stockpileRepository.save(stockpile);
            }
            
            // 更新订单状态为已取消
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }
    }
}
