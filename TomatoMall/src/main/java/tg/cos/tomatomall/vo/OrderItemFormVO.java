package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderItemFormVO {
    private Integer productId;
    private String productTitle;
    private Integer quantity;
    private BigDecimal price;
    private String cover;
    private List<ChapterGetAllVO> chapters;
    private boolean isFullyPurchased;
}