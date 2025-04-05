package tg.cos.tomatomall.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.dto.SpecificationDTO;
import tg.cos.tomatomall.dto.StockpileDTO;
import tg.cos.tomatomall.po.*;
import tg.cos.tomatomall.repository.*;
import tg.cos.tomatomall.service.ProductService;
import tg.cos.tomatomall.vo.StockPileUpdateVO;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SpecificationRepository specificationRepository;
    private final StockpileRepository stockpileRepository;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToProductVO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        return productRepository.findById(id)
                .map(this::convertToProductVO)
                .orElse(null);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Save product
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        Product savedProduct = productRepository.save(product);

        // Save specifications
        if (productDTO.getSpecifications() != null) {
            Set<Specification> specifications = productDTO.getSpecifications().stream()
                    .map(specVO -> {
                        Specification spec = new Specification();
                        BeanUtils.copyProperties(specVO, spec);
                        spec.setProduct(savedProduct);
                        return spec;
                    })
                    .collect(Collectors.toSet());
            specificationRepository.saveAll(specifications);
        }

        // Create stockpile
        Stockpile stockpile = new Stockpile();
        stockpile.setProduct(savedProduct);
        stockpile.setAmount(0);
        stockpile.setFrozen(0);
        stockpileRepository.save(stockpile);

        return convertToProductVO(savedProduct);
    }

    @Override
    @Transactional
    public String updateProduct(ProductDTO productDTO) {
//        return productRepository.findById(productDTO.getId())
//                .map(existingProduct -> {
//                    // Update product
//                    BeanUtils.copyProperties(productDTO, existingProduct, "id");
//                    Product updatedProduct = productRepository.save(existingProduct);
//
//                    // Update specifications
//                    if (productDTO.getSpecifications() != null) {
//                        // Delete existing specifications
//                        specificationRepository.deleteByProductId(updatedProduct.getId());
//
//                        // Save new specifications
//                        Set<Specification> specifications = productDTO.getSpecifications().stream()
//                                .map(specVO -> {
//                                    Specification spec = new Specification();
//                                    BeanUtils.copyProperties(specVO, spec);
//                                    spec.setProduct(updatedProduct);
//                                    return spec;
//                                })
//                                .collect(Collectors.toSet());
//                        specificationRepository.saveAll(specifications);
//                    }
//
//                    return convertToProductVO(updatedProduct);
//                })
//                .orElse(null);
        Product product;
        Optional<Product> optionalProduct = productRepository.findById(productDTO.getId());
        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        }else {
            return "商品不存在";
        }
        if (productDTO.getSpecifications() != null) {
            Set<Specification> specifications = new HashSet<>();
            for (SpecificationDTO specDTO : productDTO.getSpecifications()) {
                specifications.add(specDTO.toPO());
            }
            product.setSpecifications(specifications);
        }
        if (productDTO.getStockpile() != null) {
            product.setStockpile(productDTO.getStockpile().toPO());
        }
        if (productDTO.getCover() != null) {
            product.setCover(productDTO.getCover());
        }
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }
        if (productDTO.getRate() != null) {
            product.setRate(productDTO.getRate());
        }
        if (productDTO.getDetail() != null) {
            product.setDetail(productDTO.getDetail());
        }
        if (productDTO.getTitle() != null) {
            product.setTitle(productDTO.getTitle());
        }
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        productRepository.save(product);
        return "更新成功";
    }

    @Override
    @Transactional
    public String deleteProduct(Integer id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return "商品不存在";
        }
        // Delete specifications first
        specificationRepository.deleteByProductId(id);
        // Delete stockpile
        stockpileRepository.deleteByProductId(id);
        // Delete product
        productRepository.deleteById(id);
        return "删除成功";
    }

    @Override
    @Transactional
    public String updateStockpile(Integer productId, StockPileUpdateVO amount) {
        stockpileRepository.findByProductId(productId)
                .map(stockpile -> {
                    stockpile.setAmount(amount.getAmount());
                    Stockpile updated = stockpileRepository.save(stockpile);
                    return convertToStockpileVO(updated);
                });
        return "调整库存成功";
    }

    @Override
    public StockpileDTO getStockpile(Integer productId) {
        return stockpileRepository.findByProductId(productId)
                .map(this::convertToStockpileVO)
                .orElse(null);
    }

    private ProductDTO convertToProductVO(Product product) {
        ProductDTO vo = new ProductDTO();
        BeanUtils.copyProperties(product, vo);

        // Set specifications
        List<Specification> specs = specificationRepository.findByProductId(product.getId());
        if (specs != null) {
            vo.setSpecifications(specs.stream()
                    .map(this::convertToSpecificationVO)
                    .collect(Collectors.toSet()));
        }

        // Set stockpile
        stockpileRepository.findByProductId(product.getId())
                .ifPresent(stock -> vo.setStockpile(convertToStockpileVO(stock)));

        return vo;
    }

    private SpecificationDTO convertToSpecificationVO(Specification specification) {
        SpecificationDTO vo = new SpecificationDTO();
        BeanUtils.copyProperties(specification, vo);
        return vo;
    }

    private StockpileDTO convertToStockpileVO(Stockpile stockpile) {
        StockpileDTO vo = new StockpileDTO();
        BeanUtils.copyProperties(stockpile, vo);
        return vo;
    }
}