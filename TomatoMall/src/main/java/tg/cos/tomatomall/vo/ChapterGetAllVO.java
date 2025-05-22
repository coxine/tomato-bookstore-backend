package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChapterGetAllVO {
    private int id;
    private int productId;
    private String name;
    private int previous;
    private int next;
    private String status;
    private boolean isPurchased;
    private BigDecimal price;
}
