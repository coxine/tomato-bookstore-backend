package tg.cos.tomatomall.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.cos.tomatomall.po.*;
import tg.cos.tomatomall.repository.*;
import tg.cos.tomatomall.service.ProductService;
import tg.cos.tomatomall.vo.*;

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
    public List<ProductVO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToProductVO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductVO getProductById(Integer id) {
        return productRepository.findById(id)
                .map(this::convertToProductVO)
                .orElse(null);
    }

    @Override
    @Transactional
    public ProductVO createProduct(ProductVO productVO) {
        // Save product
        Product product = new Product();
        BeanUtils.copyProperties(productVO, product);
        Product savedProduct = productRepository.save(product);

        // Save specifications
        if (productVO.getSpecifications() != null) {
            Set<Specification> specifications = productVO.getSpecifications().stream()
                    .map(specVO -> {
                        Specification spec = new Specification();
                        BeanUtils.copyProperties(specVO, spec);
                        spec.setProductId(savedProduct.getId());
                        return spec;
                    })
                    .collect(Collectors.toSet());
            specificationRepository.saveAll(specifications);
        }

        // Create stockpile
        Stockpile stockpile = new Stockpile();
        stockpile.setProductId(savedProduct.getId());
        stockpile.setAmount(0);
        stockpile.setFrozen(0);
        stockpileRepository.save(stockpile);

        return convertToProductVO(savedProduct);
    }

    @Override
    @Transactional
    public ProductVO updateProduct(ProductVO productVO) {
        return productRepository.findById(productVO.getId())
                .map(existingProduct -> {
                    // Update product
                    BeanUtils.copyProperties(productVO, existingProduct, "id");
                    Product updatedProduct = productRepository.save(existingProduct);

                    // Update specifications
                    if (productVO.getSpecifications() != null) {
                        // Delete existing specifications
                        specificationRepository.deleteByProductId(updatedProduct.getId());

                        // Save new specifications
                        Set<Specification> specifications = productVO.getSpecifications().stream()
                                .map(specVO -> {
                                    Specification spec = new Specification();
                                    BeanUtils.copyProperties(specVO, spec);
                                    spec.setProductId(updatedProduct.getId());
                                    return spec;
                                })
                                .collect(Collectors.toSet());
                        specificationRepository.saveAll(specifications);
                    }

                    return convertToProductVO(updatedProduct);
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        // Delete specifications first
        specificationRepository.deleteByProductId(id);
        // Delete stockpile
        stockpileRepository.deleteByProductId(id);
        // Delete product
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public StockpileVO updateStockpile(Integer productId, Integer amount) {
        return stockpileRepository.findByProductId(productId)
                .map(stockpile -> {
                    stockpile.setAmount(amount);
                    Stockpile updated = stockpileRepository.save(stockpile);
                    return convertToStockpileVO(updated);
                })
                .orElse(null);
    }

    @Override
    public StockpileVO getStockpile(Integer productId) {
        return stockpileRepository.findByProductId(productId)
                .map(this::convertToStockpileVO)
                .orElse(null);
    }

    private ProductVO convertToProductVO(Product product) {
        ProductVO vo = new ProductVO();
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

    private SpecificationVO convertToSpecificationVO(Specification specification) {
        SpecificationVO vo = new SpecificationVO();
        BeanUtils.copyProperties(specification, vo);
        return vo;
    }

    private StockpileVO convertToStockpileVO(Stockpile stockpile) {
        StockpileVO vo = new StockpileVO();
        BeanUtils.copyProperties(stockpile, vo);
        return vo;
    }
}