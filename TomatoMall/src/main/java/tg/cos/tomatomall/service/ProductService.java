package tg.cos.tomatomall.service;

import tg.cos.tomatomall.vo.ProductVO;
import tg.cos.tomatomall.vo.StockpileVO;

import java.util.List;

public interface ProductService {
    List<ProductVO> getAllProducts();
    ProductVO getProductById(Integer id);
    ProductVO createProduct(ProductVO productVO);
    ProductVO updateProduct(ProductVO productVO);
    void deleteProduct(Integer id);
    StockpileVO updateStockpile(Integer productId, Integer amount);
    StockpileVO getStockpile(Integer productId);
}