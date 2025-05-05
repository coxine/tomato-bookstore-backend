package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderPayVO {
    String paymentForm;
    Integer orderId;
    BigDecimal totalAmount;
    String paymentMethod;
    String status;
}
