package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemFormVO {
    private Integer productId;
    private String productTitle;
    private Integer quantity;
    private BigDecimal price;
    private String cover;
}