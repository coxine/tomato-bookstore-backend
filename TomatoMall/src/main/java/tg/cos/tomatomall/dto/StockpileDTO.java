package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockpileDTO {
    private Integer id;
    private Integer productId;
    private Integer amount;
    private Integer frozen;
}