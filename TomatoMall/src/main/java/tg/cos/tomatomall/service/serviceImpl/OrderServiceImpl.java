package tg.cos.tomatomall.service.serviceImpl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tg.cos.tomatomall.po.Order;
import tg.cos.tomatomall.po.OrderItem;
import tg.cos.tomatomall.po.Product;
import tg.cos.tomatomall.po.Stockpile;
import tg.cos.tomatomall.repository.OrderRepository;
import tg.cos.tomatomall.service.OrderService;
import tg.cos.tomatomall.vo.OrderPayVO;


import com.alipay.api.*;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.*;
import com.alipay.api.request.*;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

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
            bizContent.put("subject", "订单支付-" + orderId);
            bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
            request.setBizContent(bizContent.toString());

            //  生成支付表单
            String form = alipayClient.pageExecute(request).getBody();

            OrderPayVO res = new OrderPayVO();
            res.setOrderId(orderId);
            res.setPaymentForm(form);
            res.setTotalAmount(order.getTotalAmount());
            res.setPaymentMethod(order.getPaymentMethod());
            return res;
        } catch (Exception e) {
            return null;
        }


    }

    @Override
    public void updateOrderStatus(String orderId, String alipayTradeNo, String amount){
        Integer id = Integer.parseInt(orderId);
        Optional<Order> orderOptional = orderRepository.findById(id);
        if(orderOptional.isEmpty()){
            return;
        }
        Order order = orderOptional.get();
        order.setAlipayTradeNo(alipayTradeNo);
        BigDecimal payAmount = new BigDecimal(amount);
        order.setPayAmount(payAmount);
        order.setStatus("SUCCESS");
        orderRepository.save(order);

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            Stockpile stockpile = product.getStockpile();
            stockpile.setFrozen(stockpile.getFrozen() - item.getQuantity());
        }
    }
}
