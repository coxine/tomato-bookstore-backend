package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class OrderDTO {
    private int id;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private Date createTime;
    private Integer accountId;
    private Set<Integer> CartItemIds;
}
