package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private int id;
    private Integer productId;
    private Integer quantity;
    private Integer accountId;
}
