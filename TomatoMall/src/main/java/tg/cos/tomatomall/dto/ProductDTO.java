package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProductDTO {
    private Integer id;
    private String title;
    private Double price;
    private Float rate;
    private String description;
    private String cover;
    private String detail;
    private Set<SpecificationDTO> specifications;
    private StockpileDTO stockpile;
}