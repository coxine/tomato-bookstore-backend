package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.po.Product;
import tg.cos.tomatomall.po.Specification;
import tg.cos.tomatomall.vo.ProductVO;
import tg.cos.tomatomall.vo.SpecificationVO;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import tg.cos.tomatomall.vo.TagVO;
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
    private Set<SpecificationVO> specifications;
    private StockpileDTO stockpile;
    private List<TagVO> tags;

//    public Product toPO(){
//        Product product = new Product();
//        if (id != null && id > 0) {
//            product.setId(id);
//        }
//        product.setTitle(title);
//        product.setPrice(price);
//        product.setRate(rate);
//        if (description != null) {
//            product.setDescription(description);
//        }
//        if (cover != null) {
//            product.setCover(cover);
//        }
//        if (detail != null) {
//            product.setDetail(detail);
//        }
//        Set<Specification> specificationSet = new HashSet<>();
//        if (specifications != null) {
//            for (SpecificationDTO specificationDTO : specifications) {
//                specificationSet.add(specificationDTO.toPO());
//            }
//        }
//        product.setSpecifications(specificationSet);
//        return product;
//    }
//    public ProductVO toVO(){
//        ProductVO product=new ProductVO();
//        product.setId(id);
//        product.setTitle(title);
//        product.setPrice(price);
//        product.setRate(rate);
//        product.setDescription(description);
//        product.setCover(cover);
//        product.setDetail(detail);
//        Set<SpecificationDTO> specificationSet = new HashSet<>(specifications);
//        product.setSpecifications(specificationSet);
//        return product;
//    }
}