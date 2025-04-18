package tg.cos.tomatomall.service;


import tg.cos.tomatomall.vo.OrderPayVO;

public interface OrderService {
    OrderPayVO pay(Integer orderId);

    void updateOrderStatus(String orderId, String alipayTradeNo, String amount,String status);
}
