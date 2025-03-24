package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockpileVO {
    private Integer id;
    private Integer productId;
    private Integer amount;
    private Integer frozen;
}