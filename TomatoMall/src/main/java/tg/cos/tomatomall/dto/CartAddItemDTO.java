package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartAddItemDTO {
    private int cartItemId;
    private int productId;
    private String title;
    private BigDecimal price;
    private String description;
    private String cover;
    private String detail;
    private int quantity;
}
