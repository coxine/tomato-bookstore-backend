package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderFormsVO {
    private Integer orderId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private Date createTime;
    private String name;
    private String address;
    private String phone;
    private List<OrderItemFormVO> orderItems;
}


