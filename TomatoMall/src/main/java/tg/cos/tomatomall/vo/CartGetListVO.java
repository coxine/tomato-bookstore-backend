package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class CartGetListVO {
    private Set<CartAddItemVO> items;
    private Integer total;
    private BigDecimal totalAmount;
}
