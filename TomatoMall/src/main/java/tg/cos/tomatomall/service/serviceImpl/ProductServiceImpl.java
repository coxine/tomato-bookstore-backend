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

import java.util.List;
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
        stockpile.setProductId(savedProduct.getId());
        stockpile.setAmount(0);
        stockpile.setFrozen(0);
        stockpileRepository.save(stockpile);

        return convertToProductVO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {
        return productRepository.findById(productDTO.getId())
                .map(existingProduct -> {
                    // Update product
                    BeanUtils.copyProperties(productDTO, existingProduct, "id");
                    Product updatedProduct = productRepository.save(existingProduct);

                    // Update specifications
                    if (productDTO.getSpecifications() != null) {
                        // Delete existing specifications
                        specificationRepository.deleteByProductId(updatedProduct.getId());

                        // Save new specifications
                        Set<Specification> specifications = productDTO.getSpecifications().stream()
                                .map(specVO -> {
                                    Specification spec = new Specification();
                                    BeanUtils.copyProperties(specVO, spec);
                                    spec.setProduct(updatedProduct);
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
    public StockpileDTO updateStockpile(Integer productId, Integer amount) {
        return stockpileRepository.findByProductId(productId)
                .map(stockpile -> {
                    stockpile.setAmount(amount);
                    Stockpile updated = stockpileRepository.save(stockpile);
                    return convertToStockpileVO(updated);
                })
                .orElse(null);
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