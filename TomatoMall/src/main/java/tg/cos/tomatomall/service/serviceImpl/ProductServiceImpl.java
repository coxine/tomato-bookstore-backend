package tg.cos.tomatomall.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.dto.StockpileDTO;
import tg.cos.tomatomall.po.*;
import tg.cos.tomatomall.repository.*;
import tg.cos.tomatomall.service.ProductService;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.ProductVO;
import tg.cos.tomatomall.vo.SpecificationVO;

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
                List<SpecificationVO> specs = new ArrayList<>();
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
            List<SpecificationVO> specs = new ArrayList<>();
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
        if (productDTO.getCover().startsWith("https") && !account.getUploadProductCoverCreates().isEmpty()) {
            account.getUploadProductCoverCreates().removeLast();
        }
        accountRepository.save(account);
        Product savedProduct = productRepository.save(product);

        // Save specifications
        if (productDTO.getSpecifications() != null) {
            Set<Specification> specifications = productDTO.getSpecifications().stream()
                    .map(specVO -> {
                        Specification spec = new Specification();
                        spec.setValue(specVO.getValue());
                        spec.setItem(specVO.getItem());
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
    public String updateProduct(ProductDTO productDTO) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }

        Product product;
        Optional<Product> optionalProduct = productRepository.findById(productDTO.getId());
        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        }else {
            return "商品不存在";
        }
        if (productDTO.getSpecifications() != null) {
            product.getSpecifications().clear();
            List<Specification> specifications = product.getSpecifications();
            for (SpecificationVO specVO : productDTO.getSpecifications()) {
                Specification specification = new Specification();
                specification.setProduct(product);
                if (specVO.getId() != null) {
                    specification.setId(specVO.getId());
                }
                specification.setItem(specVO.getItem());
                specification.setValue(specVO.getValue());
                specifications.add(specification);
            }
            product.setSpecifications(specifications);
        }
        if (productDTO.getCover() != null) {
            product.setCover(productDTO.getCover());
            product.setLastChangeCover(new Date());
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
    public String updateStockpile(Integer productId, StockpileDTO stockpileDTO) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }
        stockpileRepository.findByProductId(productId)
                .map(stockpile -> {
                    stockpile.setAmount(stockpileDTO.getAmount());
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
                    .collect(Collectors.toList()));
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


    @Override
    public float postRate(Integer productId, float rate) {
        Account account = securityUtil.getCurrentUser();
        Set<Integer> rateProducts = account.getRateProducts();
        if (rateProducts == null) {
            rateProducts = new HashSet<>();
        }
        if (rateProducts.contains(productId)) {
            return -3;
        }
        if (rate < 0 || rate > 10) {
            return -2;
        }
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return -1;
        }
        Product product = optionalProduct.get();
        Integer number = product.getRatePeopleNumber();
        if (number == null){
            number = 0;
        }
        float newRate = number * product.getRate();
        newRate += rate;
        number++;
        newRate /= number;
        product.setRate(newRate);
        product.setRatePeopleNumber(number);
        productRepository.save(product);
        rateProducts.add(productId);
        account.setRateProducts(rateProducts);
        accountRepository.save(account);
        return newRate;
    }
}