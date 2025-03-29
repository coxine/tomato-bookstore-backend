package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.po.Specification;

@Getter
@Setter
public class SpecificationDTO {
    private Integer id;
    private String item;
    private String value;
    private ProductDTO product;

    public Specification toPO(){
        Specification spec = new Specification();
        spec.setId(id);
        spec.setItem(item);
        spec.setValue(value);
        spec.setProduct(product.toPO());
        return spec;
    }
}