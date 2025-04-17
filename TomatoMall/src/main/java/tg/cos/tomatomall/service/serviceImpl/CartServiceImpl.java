package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tg.cos.tomatomall.po.*;
import tg.cos.tomatomall.repository.*;
import tg.cos.tomatomall.service.CartService;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.CartAddItemVO;
import tg.cos.tomatomall.vo.CartCheckOutInputVO;
import tg.cos.tomatomall.vo.CartCheckOutOutputVO;
import tg.cos.tomatomall.vo.CartGetListVO;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    private StockpileRepository stockpileRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public CartAddItemVO addItem(CartAddItemVO cartAddItemVO){
        CartAddItemVO result = new CartAddItemVO();
        Account account = securityUtil.getCurrentUser();
        List<CartItem> cartItems = account.getCartItems();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId() == cartAddItemVO.getProductId()) {
                Product product = cartItem.getProduct();
                Stockpile stockpile = product.getStockpile();
                if (cartItem.getQuantity() < cartAddItemVO.getQuantity()) {
                    if (stockpile.getAmount() < cartAddItemVO.getQuantity()) {
                        return null;
                    }
                }
                stockpile.setAmount(stockpile.getAmount() - cartAddItemVO.getQuantity());
                stockpile.setFrozen(stockpile.getFrozen() + cartAddItemVO.getQuantity());
                cartItem.setQuantity(cartItem.getQuantity() + cartAddItemVO.getQuantity());
                accountRepository.save(account);
                stockpileRepository.save(stockpile);
                cartItemRepository.save(cartItem);
                result.setProductId(product.getId());
                result.setCartItemId(cartItem.getId());
                result.setTitle(product.getTitle());
                result.setPrice(product.getPrice());
                String cover = product.getCover();
                if (cover != null){
                    result.setCover(cover);
                }
                String description = product.getDescription();
                if (description != null){
                    result.setDescription(description);
                }
                String detail = product.getDetail();
                if (detail != null){
                    result.setDetail(detail);
                }
                result.setQuantity(cartAddItemVO.getQuantity());
                return result;
            }
        }
        int productId = cartAddItemVO.getProductId();
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product;
        if(productOptional.isPresent()){
            product = productOptional.get();
        }else {
            return null;
        }
        int quantity = cartAddItemVO.getQuantity();
        if (quantity > product.getStockpile().getAmount()){
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setAccount(account);
        cartItemRepository.save(cartItem);

        account.getCartItems().add(cartItem);
        accountRepository.save(account);

        result.setProductId(productId);
        result.setCartItemId(cartItem.getId());
        result.setTitle(product.getTitle());
        result.setPrice(product.getPrice());
        String cover = product.getCover();
        if (cover != null){
            result.setCover(cover);
        }
        String description = product.getDescription();
        if (description != null){
            result.setDescription(description);
        }
        String detail = product.getDetail();
        if (detail != null){
            result.setDetail(detail);
        }
        result.setQuantity(quantity);
        return result;
    }

    @Override
    public String deleteItem(Integer id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isEmpty()){
            return "购物车商品不存在";
        }

        cartItemRepository.deleteById(id);
        return "删除成功";
    }

    @Override
    public String updateItem(Integer id, CartAddItemVO cartAddItemVO) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isEmpty()){
            return "购物车商品不存在";
        }
        CartItem cartItem = cartItemOptional.get();
        Integer quantity = cartItem.getQuantity();
        Product product = cartItem.getProduct();
        Stockpile stockpile = product.getStockpile();
        if (quantity > cartAddItemVO.getQuantity()){
            Integer newQuantity = quantity - cartAddItemVO.getQuantity();
            stockpile.setAmount(stockpile.getAmount()+newQuantity);
            stockpile.setFrozen(stockpile.getFrozen()-newQuantity);
        }else {
            Integer newQuantity = cartAddItemVO.getQuantity() - quantity;
            if (newQuantity > stockpile.getAmount()){
                return "库存不足";
            }else {
                stockpile.setAmount(stockpile.getAmount()-newQuantity);
                stockpile.setFrozen(stockpile.getFrozen()+newQuantity);
            }
        }
        stockpileRepository.save(stockpile);
        cartItem.setQuantity(cartAddItemVO.getQuantity());
        cartItemRepository.save(cartItem);
        return "修改数量成功";
    }

    @Override
    public CartGetListVO getList(){
        Account account = securityUtil.getCurrentUser();
        List<CartItem> cartItems = account.getCartItems();

        CartGetListVO result = new CartGetListVO();

        int total = 0;
        BigDecimal amount = new BigDecimal(0);
        List<CartAddItemVO> cartAddItemVOSet = new ArrayList<>();

        for (CartItem cartItem : cartItems){
            total++;
            CartAddItemVO addItemVO = new CartAddItemVO();
            addItemVO.setCartItemId(cartItem.getId());
            addItemVO.setProductId(cartItem.getProduct().getId());
            addItemVO.setTitle(cartItem.getProduct().getTitle());
            BigDecimal price = cartItem.getProduct().getPrice();
            addItemVO.setPrice(price);
            String description = cartItem.getProduct().getDescription();
            if (description != null){
                addItemVO.setDescription(description);
            }
            String detail = cartItem.getProduct().getDetail();
            if (detail != null){
                addItemVO.setDetail(detail);
            }
            String cover = cartItem.getProduct().getCover();
            if (cover != null){
                addItemVO.setCover(cover);
            }
            int quantity = cartItem.getQuantity();
            addItemVO.setQuantity(quantity);
            cartAddItemVOSet.add(addItemVO);
            BigDecimal DecimalQuantity = new BigDecimal(quantity);
            amount = amount.add(DecimalQuantity.multiply(price));
        }
        result.setTotal(total);
        result.setItems(cartAddItemVOSet);
        result.setTotalAmount(amount);
        return result;
    }

    @Override
    public CartCheckOutOutputVO checkout(CartCheckOutInputVO cartCheckOutInputVO){
//        System.out.println("进入checkout Service");
        Account account = securityUtil.getCurrentUser();
        Order order = new Order();
        order.setAccount(account);
        // TODO: 检查paymentMethod是否合法
        order.setPaymentMethod(cartCheckOutInputVO.getPayment_method());
        order.setName(cartCheckOutInputVO.getShipping_address().getName());
        order.setAddress(cartCheckOutInputVO.getShipping_address().getAddress());
        order.setPhone(cartCheckOutInputVO.getShipping_address().getPhone());
        Set<CartItem> cartItems = new HashSet<>();

        // 查看购买的商品
        for (Integer cartItemId : cartCheckOutInputVO.getCartItemIds()) {
            Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
            if (cartItemOptional.isEmpty()){
//                System.out.println("查询不到购物车商品");
                return null;
            }
            CartItem cartItem = cartItemOptional.get();
            if (cartItem.getAccount().getId() != account.getId()){// 如果购物车中的货物关联的账号和登录的账号不一致
//                System.out.println("购物者和登陆者身份不一致, 登陆者id为" + account.getId()+",购物者id为"+cartItem.getAccount().getId());
                return null;
            }
            cartItems.add(cartItem);
        }

        //确认订单已经没有问题了
//        System.out.println("checkout 检查完毕");

        BigDecimal total = new BigDecimal(0);
        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cartItems){

            Product product = cartItem.getProduct();

            total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            Stockpile stockpile = product.getStockpile();
            stockpile.setAmount(stockpile.getAmount() - cartItem.getQuantity());
            stockpile.setFrozen(stockpile.getFrozen() + cartItem.getQuantity());// 冻结库存
            // TODO: 完成库存的解冻
            product.setStockpile(stockpile);
            stockpileRepository.save(stockpile);
            productRepository.save(product);
            cartItemRepository.save(cartItem);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setAccount(account);
            orderItems.add(orderItem);
            account.getOrderItems().add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(total);
        order.setStatus("PENDING");
        order.setCreateTime(new Date());
        orderRepository.save(order);
        account.getOrders().add(order);
        accountRepository.save(account);
        orderItemRepository.saveAll(order.getOrderItems());

        CartCheckOutOutputVO result = new CartCheckOutOutputVO();
        result.setOrderId(order.getId());
        result.setUsername(account.getUsername());
        result.setTotalAmount(total);
        result.setPaymentMethod(cartCheckOutInputVO.getPayment_method());
        result.setCreateTime(order.getCreateTime());
        result.setStatus(order.getStatus());
        return result;
    }
}
