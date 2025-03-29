package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.po.Product;
import tg.cos.tomatomall.po.Specification;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ProductDTO {
    private Integer id;
    private String title;
    private BigDecimal price;
    private Float rate;
    private String description;
    private String cover;
    private String detail;
    private Set<SpecificationDTO> specifications;
    private StockpileDTO stockpile;

    public Product toPO(){
        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setPrice(price);
        product.setRate(rate);
        product.setDescription(description);
        product.setCover(cover);
        product.setDetail(detail);
        Set<Specification> specificationSet = new HashSet<>();
        for (SpecificationDTO specificationDTO : specifications) {
            specificationSet.add(specificationDTO.toPO());
        }
        product.setSpecifications(specificationSet);
        return product;
    }
}