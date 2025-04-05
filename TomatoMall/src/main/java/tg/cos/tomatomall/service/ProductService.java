package tg.cos.tomatomall.service;

import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.dto.StockpileDTO;
import tg.cos.tomatomall.vo.StockPileUpdateVO;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Integer id);
    ProductDTO createProduct(ProductDTO productDTO);
    String updateProduct(ProductDTO productDTO);
    String deleteProduct(Integer id);
    String updateStockpile(Integer productId, StockPileUpdateVO amount);
    StockpileDTO getStockpile(Integer productId);
}