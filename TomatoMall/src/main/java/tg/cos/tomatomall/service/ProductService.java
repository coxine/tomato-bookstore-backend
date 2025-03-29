package tg.cos.tomatomall.service;

import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.dto.StockpileDTO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Integer id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(ProductDTO productDTO);
    void deleteProduct(Integer id);
    StockpileDTO updateStockpile(Integer productId, Integer amount);
    StockpileDTO getStockpile(Integer productId);
}