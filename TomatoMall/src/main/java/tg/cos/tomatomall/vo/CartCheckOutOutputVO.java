package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class CartCheckOutOutputVO {
    Integer orderId;
    String username;
    BigDecimal totalAmount;
    String paymentMethod;
    Date createTime;
    String status;
}
