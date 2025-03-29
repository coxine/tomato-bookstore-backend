package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.po.Stockpile;

@Getter
@Setter
public class StockpileDTO {
    private Integer id;
    private ProductDTO product;
    private Integer amount;
    private Integer frozen;

    public Stockpile toPO(){
        Stockpile stockpile = new Stockpile();
        stockpile.setId(id);
        stockpile.setProduct(product.toPO());
        stockpile.setAmount(amount);
        stockpile.setFrozen(frozen);
        return stockpile;
    }
}