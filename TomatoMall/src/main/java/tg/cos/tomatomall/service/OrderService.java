package tg.cos.tomatomall.service;

import tg.cos.tomatomall.vo.OrderPayVO;
import tg.cos.tomatomall.vo.OrderFormsVO;
import java.util.List;

public interface OrderService {
    OrderPayVO pay(Integer orderId);

    void updateOrderStatus(String orderId, String alipayTradeNo, String amount,String status);
    
    List<OrderFormsVO> getUserOrders(Integer accountId);
    
    List<OrderFormsVO> getAllOrders();
}
