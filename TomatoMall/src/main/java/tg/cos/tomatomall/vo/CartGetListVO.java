package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CartGetListVO {
    private List<CartAddItemVO> items;
    private Integer total;
    private BigDecimal totalAmount;
}
