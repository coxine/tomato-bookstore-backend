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
import tg.cos.tomatomall.vo.TagVO;

import java.util.*;
import java.util.stream.Collectors;

import static tg.cos.tomatomall.enums.ProductPostRateEnum.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SpecificationRepository specificationRepository;
    private final StockpileRepository stockpileRepository;
    private final TagRepository tagRepository;

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
            
            // 处理标签
            if (product.getTags() != null && !product.getTags().isEmpty()) {
                List<TagVO> tagVOs = product.getTags().stream()
                    .map(tag -> {
                        TagVO tagVO = new TagVO();
                        tagVO.setId(tag.getId());
                        tagVO.setName(tag.getName());
                        return tagVO;
                    })
                    .collect(Collectors.toList());
                productVO.setTags(tagVOs);
            }
            
            result.add(productVO);
        }
        return result;
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
        
        // 处理标签
        if (product.getTags() != null && !product.getTags().isEmpty()) {
            List<TagVO> tagVOs = product.getTags().stream()
                .map(tag -> {
                    TagVO tagVO = new TagVO();
                    tagVO.setId(tag.getId());
                    tagVO.setName(tag.getName());
                    return tagVO;
                })
                .collect(Collectors.toList());
            productVO.setTags(tagVOs);
        }
        
        return productVO;
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
        if (productDTO.getCover()!=null&&productDTO.getCover().startsWith("https") && !account.getUploadProductCoverCreates().isEmpty()) {
            account.getUploadProductCoverCreates().removeLast();
        }
        accountRepository.save(account);
        
        // 处理标签
        if (productDTO.getTags() != null ) {
            List<Tag> tags = new ArrayList<>();
            for (TagVO tagVO : productDTO.getTags()) {
                Tag tag = null;
                
                // 情况1: 有ID，按ID查找
                if (tagVO.getId() != null) {
                    Optional<Tag> optionalTag = tagRepository.findById(tagVO.getId());
                    if (optionalTag.isPresent()) {
                        tag = optionalTag.get();
                    }
                }
                
                // 情况2: 有name，按name查找
                if (tag == null && tagVO.getName() != null && !tagVO.getName().isEmpty()) {
                    Optional<Tag> optionalTag = tagRepository.findByName(tagVO.getName());
                    if (optionalTag.isPresent()) {
                        tag = optionalTag.get();
                    }
                
                    // 如果不存在，则创建新标签
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagVO.getName());
                        tag = tagRepository.save(tag);
                    }
                }
                
                // 如果找到或创建了标签，则添加到列表
                if (tag != null) {
                    tags.add(tag);
                }
            }
            product.setTags(tags);
        }
        
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
    @Transactional
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

        // 处理标签

        if (productDTO.getTags() != null ) {
            List<Tag> tags = new ArrayList<>();
            for (TagVO tagVO : productDTO.getTags()) {
                Tag tag = null;
                
                // 情况1: 有ID，按ID查找
                if (tagVO.getId() != null) {
                    Optional<Tag> optionalTag = tagRepository.findById(tagVO.getId());
                    if (optionalTag.isPresent()) {
                        tag = optionalTag.get();
                    }
                }
                
                // 情况2: 有name，按name查找
                if (tag == null && tagVO.getName() != null && !tagVO.getName().isEmpty()) {
                    Optional<Tag> optionalTag = tagRepository.findByName(tagVO.getName());
                    if (optionalTag.isPresent()) {
                        tag = optionalTag.get();
                    }
                
                    // 如果不存在，则创建新标签
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagVO.getName());
                        tag = tagRepository.save(tag);
                    }
                }
                
                // 如果找到或创建了标签，则添加到列表
                if (tag != null) {
                    tags.add(tag);
                }
            }
            product.setTags(tags);
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
        
        // 处理标签
        if (product.getTags() != null && !product.getTags().isEmpty()) {
            List<TagVO> tagVOs = product.getTags().stream()
                .map(tag -> {
                    TagVO tagVO = new TagVO();
                    tagVO.setId(tag.getId());
                    tagVO.setName(tag.getName());
                    return tagVO;
                })
                .collect(Collectors.toList());
            vo.setTags(tagVOs);
        }

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
            return REPEATED.getValue();
        }
        if (rate < 0 || rate > 10) {
            return OUTRANGE.getValue();
        }
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return FINDFAIL.getValue();
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

    @Override
    public List<ProductVO> getProductsByTagId(Integer tagId) {
        Optional<Tag> tagOptional = tagRepository.findById(tagId);
        if (tagOptional.isEmpty()) {
            return Collections.emptyList();
        }

        Tag tag = tagOptional.get();
        List<Product> products = tag.getProducts();
        
        return products.stream()
            .map(this::convertToProductVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductVO> getProductsByRatingDesc() {
        List<Product> products = productRepository.findAll();
        
        // 按评分从高到低排序
        products.sort((p1, p2) -> Float.compare(p2.getRate(), p1.getRate()));
        
        return products.stream()
            .map(this::convertToProductVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductVO> getTopNProductsByRating(Integer n) {
        if (n <= 0) {
            return Collections.emptyList();
        }
        
        List<Product> products = productRepository.findAll();
        
        // 按评分从高到低排序
        products.sort((p1, p2) -> Float.compare(p2.getRate(), p1.getRate()));
        
        // 取前n个，如果商品总数小于n，则取所有商品
        int limit = Math.min(n, products.size());
        
        return products.stream()
            .limit(limit)
            .map(this::convertToProductVO)
            .collect(Collectors.toList());
    }
}