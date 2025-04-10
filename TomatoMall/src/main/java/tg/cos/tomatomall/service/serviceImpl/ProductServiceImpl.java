package tg.cos.tomatomall.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.dto.SpecificationDTO;
import tg.cos.tomatomall.dto.StockpileDTO;
import tg.cos.tomatomall.po.*;
import tg.cos.tomatomall.repository.*;
import tg.cos.tomatomall.service.ProductService;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.ProductVO;
import tg.cos.tomatomall.vo.SpecificationVO;
import tg.cos.tomatomall.vo.StockPileUpdateVO;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SpecificationRepository specificationRepository;
    private final StockpileRepository stockpileRepository;

    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<ProductVO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return Collections.emptyList();
        }
        List<ProductVO> result = new ArrayList<>();
        for (Product product : products) {
            ProductVO productVO = new ProductVO();
            productVO.setId(product.getId());
            productVO.setTitle(product.getTitle());
            productVO.setPrice(product.getPrice());
            productVO.setRate(product.getRate());
            if (product.getSpecifications() != null) {
                Set<SpecificationVO> specs = new HashSet<>();
                for (Specification specification : product.getSpecifications()) {
                    SpecificationVO specificationVO = new SpecificationVO();
                    specificationVO.setId(specification.getId());
                    specificationVO.setProductId(specification.getProduct().getId());
                    specificationVO.setItem(specification.getItem());
                    specificationVO.setValue(specification.getValue());
                    specs.add(specificationVO);
                }
                productVO.setSpecifications(specs);
            }
            if (product.getCover() != null) {
                productVO.setCover(product.getCover());
            }
            if (product.getDescription() != null) {
                productVO.setDescription(product.getDescription());
            }
            if (product.getDetail() != null) {
                productVO.setDetail(product.getDetail());
            }
            result.add(productVO);
        }
        return result;
//        return productRepository.findAll().stream()
//                .map(this::convertToProductVO)
//                .collect(Collectors.toList());
    }

    @Override
    public ProductVO getProductById(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return null;
        }
        ProductVO productVO = new ProductVO();
        productVO.setId(product.getId());
        productVO.setTitle(product.getTitle());
        productVO.setPrice(product.getPrice());
        productVO.setRate(product.getRate());
        if (product.getSpecifications() != null) {
            Set<SpecificationVO> specs = new HashSet<>();
            for (Specification specification : product.getSpecifications()) {
                SpecificationVO specificationVO = new SpecificationVO();
                specificationVO.setId(specification.getId());
                specificationVO.setProductId(specification.getProduct().getId());
                specificationVO.setItem(specification.getItem());
                specificationVO.setValue(specification.getValue());
                specs.add(specificationVO);
            }
            productVO.setSpecifications(specs);
        }
        if (product.getCover() != null) {
            productVO.setCover(product.getCover());
        }
        if (product.getDescription() != null) {
            productVO.setDescription(product.getDescription());
        }
        if (product.getDetail() != null) {
            productVO.setDetail(product.getDetail());
        }
        return productVO;

//        return productRepository.findById(id)
//                .map(this::convertToProductVO)
//                .orElse(null);
    }

    @Override
    @Transactional
    public ProductVO createProduct(ProductDTO productDTO) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return null;
        }
        // Save product
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product.setLastChangeCover(new Date());
        if (productDTO.getCover().startsWith("https")) {
            account.getUploadProductCoverCreates().remove(account.getUploadProductCoverCreates().size()-1);
        }
        accountRepository.save(account);
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
    public String updateProduct(ProductVO productVO) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }
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
        Optional<Product> optionalProduct = productRepository.findById(productVO.getId());
        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        }else {
            return "商品不存在";
        }
        if (productVO.getSpecifications() != null) {
            Set<Specification> specifications = product.getSpecifications();
            for (SpecificationVO specVO : productVO.getSpecifications()) {
                Specification specification = new Specification();
                specification.setProduct(product);
                if (specVO.getId() != null) {
                    specification.setId(specVO.getId());
                    for (Specification specification1: specifications){
                        if (specification1.getId().equals(specVO.getId())){
                            specifications.remove(specification1);
                        }
                    }
                }
                specification.setItem(specVO.getItem());
                specification.setValue(specVO.getValue());
                specifications.add(specification);
            }
            product.setSpecifications(specifications);
        }
        if (productVO.getCover() != null) {
            product.setCover(productVO.getCover());
            product.setLastChangeCover(new Date());
        }
        if (productVO.getPrice() != null) {
            product.setPrice(productVO.getPrice());
        }
        if (productVO.getRate() != null) {
            product.setRate(productVO.getRate());
        }
        if (productVO.getDetail() != null) {
            product.setDetail(productVO.getDetail());
        }
        if (productVO.getTitle() != null) {
            product.setTitle(productVO.getTitle());
        }
        if (productVO.getDescription() != null) {
            product.setDescription(productVO.getDescription());
        }
        productRepository.save(product);
        return "更新成功";
    }

    @Override
    @Transactional
    public String deleteProduct(Integer id) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }
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
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }
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
//        stockpileRepository.findByProductId(product.getId())
//                .ifPresent(stock -> vo.setStockpile(convertToStockpileVO(stock)));

        return vo;
    }

    private SpecificationVO convertToSpecificationVO(Specification specification) {
        SpecificationVO vo = new SpecificationVO();
        BeanUtils.copyProperties(specification, vo);
        return vo;
    }

    private StockpileDTO convertToStockpileVO(Stockpile stockpile) {
        StockpileDTO vo = new StockpileDTO();
        BeanUtils.copyProperties(stockpile, vo);
        return vo;
    }
}