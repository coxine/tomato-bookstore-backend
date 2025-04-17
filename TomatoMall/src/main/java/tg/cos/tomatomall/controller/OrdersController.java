package tg.cos.tomatomall.controller;

import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tg.cos.tomatomall.service.OrderService;
import tg.cos.tomatomall.vo.OrderPayVO;
import tg.cos.tomatomall.vo.Response;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;


import com.alipay.api.internal.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @Value("${alipay.alipay-public-key}")
    private String ALIPAY_PUBLIC_KEY;

    @PostMapping("/{orderId}/pay")
    public Response<?> pay(@PathVariable("orderId") Integer orderId) {
        OrderPayVO res = orderService.pay(orderId);
        if (res == null) {
            return Response.buildFailure("发起支付失败","400");
        }
        return Response.buildSuccess(res);
    }

    @PostMapping("/notify")
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 解析支付宝回调参数（通常是 application/x-www-form-urlencoded）
        Map<String, String>     params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
        System.err.println(params);
        // 2. 验证支付宝签名（防止伪造请求）
        boolean signVerified = false;
        try {

            signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, "UTF-8", "RSA2");
        }catch (Exception e){
            e.printStackTrace();
        }
        if (!signVerified) {
            response.getWriter().print("fail"); // 签名验证失败，返回 fail
            return;
        }

        // 3. 处理业务逻辑（更新订单、减库存等）
        String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            String orderId = params.get("out_trade_no"); // 您的订单号
            String alipayTradeNo = params.get("trade_no"); // 支付宝交易号
            String amount = params.get("total_amount"); // 支付金额

            // 更新订单状态（注意幂等性，防止重复处理）
            orderService.updateOrderStatus(orderId, alipayTradeNo, amount);

            // 扣减库存（建议加锁或乐观锁）
//            inventoryService.reduceStock(orderId);
        }

        // 4. 必须返回纯文本的 "success"（支付宝要求）
        response.getWriter().print("success");
    }


    @GetMapping("/returnUrl")
    public String returnUrl() {
        return "支付成功了";
    }
}
