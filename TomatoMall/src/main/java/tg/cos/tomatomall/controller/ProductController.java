package tg.cos.tomatomall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tg.cos.tomatomall.service.ProductService;
import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.vo.Response;
import tg.cos.tomatomall.dto.StockpileDTO;
import tg.cos.tomatomall.vo.StockPileUpdateVO;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取所有商品信息
     */
    @GetMapping
    public Response<?> getAllProducts() {
        return Response.buildSuccess(productService.getAllProducts());
    }

    /**
     * 获取指定商品信息
     */
    @GetMapping("/{id}")
    public Response<?> getProductById(@PathVariable("id") Integer id) {
        ProductDTO product = productService.getProductById(id);
        if (product != null) {
            return Response.buildSuccess(product);
        }
        return Response.buildFailure("商品不存在", "400");
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Response<?> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        if (createdProduct != null) {
            return Response.buildSuccess(createdProduct);
        }
        return Response.buildFailure("创建商品失败", "400");
    }

    /**
     * 更新商品信息
     */
    @PutMapping
    public Response<?> updateProduct(@RequestBody ProductDTO productDTO) {
        String msg = productService.updateProduct(productDTO);
        if (msg.equals("更新成功")) {
            return Response.buildSuccess(msg);
        }
        return Response.buildFailure("商品不存在", "400");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Response<?> deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return Response.buildSuccess("删除成功");
    }

    /**
     * 调整指定商品的库存
     */
    @PatchMapping("/stockpile/{productId}")
    public Response<?> updateStockpile(
            @PathVariable("productId") Integer productId,
            @RequestBody StockPileUpdateVO amount) {
        String msg = productService.updateStockpile(productId, amount);
        if (msg.equals("调整库存成功")) {
            return Response.buildSuccess(msg);
        }
        return Response.buildFailure("商品不存在", "400");
    }

    /**
     * 查询指定商品的库存
     */
    @GetMapping("/stockpile/{productId}")
    public Response<?> getStockpile(@PathVariable("productId") Integer productId) {
        StockpileDTO stockpile = productService.getStockpile(productId);
        if (stockpile != null) {
            return Response.buildSuccess(stockpile);
        }
        return Response.buildFailure("商品不存在", "404");
    }
}