package tg.cos.tomatomall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tg.cos.tomatomall.service.ProductService;
import tg.cos.tomatomall.dto.ProductDTO;
import tg.cos.tomatomall.vo.ProductVO;
import tg.cos.tomatomall.vo.Response;
import tg.cos.tomatomall.dto.StockpileDTO;
import java.util.List;

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
        ProductVO product = productService.getProductById(id);
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
        ProductVO createdProduct = productService.createProduct(productDTO);
        if (createdProduct != null) {
            return Response.buildSuccess(createdProduct);
        }
        return Response.buildFailure("创建商品失败", "400");
    }

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
        String result = productService.deleteProduct(id);
        if (result.equals("删除成功")) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result, "400");
    }

    /**
     * 调整指定商品的库存
     */
    @PatchMapping("/stockpile/{productId}")
    public Response<?> updateStockpile(
            @PathVariable("productId") Integer productId,
            @RequestBody StockpileDTO stockpileDTO) {
        String msg = productService.updateStockpile(productId, stockpileDTO);
        if (msg.equals("调整库存成功")) {
            return Response.buildSuccess(msg);
        }
        return Response.buildFailure(msg, "400");
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

    @PostMapping("/{productId}/rate/{rate}")
    public Response<?> postRate(@PathVariable("productId") Integer productId,
                                @PathVariable("rate") float rate) {
        float newRate = productService.postRate(productId, rate);
        if (newRate == -1 ){
            return Response.buildFailure("查找不到该商品","400");
        }else if (newRate == -2 ){
            return Response.buildFailure("评分应该在0-10范围内","400");
        }else if(newRate == -3){
            return Response.buildFailure("该用户已评分","400");
        }else {
            return Response.buildSuccess(newRate);
        }
    }

    /**
     * 根据标签ID获取商品列表
     */
    @GetMapping("/byTag/{tagId}")
    public Response<?> getProductsByTagId(@PathVariable("tagId") Integer tagId) {
        List<ProductVO> products = productService.getProductsByTagId(tagId);
        return Response.buildSuccess(products);
    }
}