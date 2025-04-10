package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.po.Specification;

@Getter
@Setter
public class SpecificationVO {
    private Integer id;
    private String item;
    private String value;
    private Integer productId;
}