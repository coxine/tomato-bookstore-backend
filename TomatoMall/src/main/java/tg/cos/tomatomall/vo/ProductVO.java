package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.dto.SpecificationDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@Getter
@Setter
public class ProductVO {
    private Integer id;
    private String title;
    private BigDecimal price;
    private Float rate;
    private String description;
    private String cover;
    private String detail;
    private List<SpecificationVO> specifications;

}
