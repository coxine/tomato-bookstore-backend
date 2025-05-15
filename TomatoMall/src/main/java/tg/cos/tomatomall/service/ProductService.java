package tg.cos.tomatomall.service;

import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.dto.StockpileDTO;
import tg.cos.tomatomall.vo.ProductVO;

import java.util.List;

public interface ProductService {
    List<ProductVO> getAllProducts();
    ProductVO getProductById(Integer id);
    ProductVO createProduct(ProductDTO productDTO);
    String updateProduct(ProductDTO productDTO);
    String deleteProduct(Integer id);
    String updateStockpile(Integer productId, StockpileDTO stockpileDTO);
    StockpileDTO getStockpile(Integer productId);
    float postRate(Integer productId, float rate);
    List<ProductVO> getProductsByTagId(Integer tagId);
    List<ProductVO> getProductsByRatingDesc();
}