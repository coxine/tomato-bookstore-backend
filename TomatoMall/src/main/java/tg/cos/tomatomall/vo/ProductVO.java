package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProductVO {
    private Integer id;
    private String title;
    private Double price;
    private Float rate;
    private String description;
    private String cover;
    private String detail;
    private Set<SpecificationVO> specifications;
    private StockpileVO stockpile;
}