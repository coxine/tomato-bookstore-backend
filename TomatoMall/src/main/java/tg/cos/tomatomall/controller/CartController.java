package tg.cos.tomatomall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tg.cos.tomatomall.dto.CartAddItemDTO;
import tg.cos.tomatomall.dto.CartCheckOutInputDTO;
import tg.cos.tomatomall.service.CartService;
import tg.cos.tomatomall.vo.CartAddItemVO;
import tg.cos.tomatomall.vo.CartCheckOutOutputVO;
import tg.cos.tomatomall.vo.Response;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public Response<?> addItem(@RequestBody CartAddItemDTO cartAddItemDTO) {
        CartAddItemVO res =  cartService.addItem(cartAddItemDTO);
        if (res != null) {
            return Response.buildSuccess(res);
        }else {
            return Response.buildFailure("加入购物车的数量超出库存","400");
        }
    }

    @DeleteMapping("/{cartItemId}")
    public Response<?> deleteItem(@PathVariable("cartItemId") int cartItemId) {
        String res =  cartService.deleteItem(cartItemId);
        if (res.equals("删除成功")){
            return Response.buildSuccess(res);
        }else {
            return Response.buildFailure(res,"400");
        }
    }

    @PatchMapping("/{cartItemId}")
    public Response<?> updateItem(@PathVariable("cartItemId") Integer cartItemId, @RequestBody CartAddItemDTO cartAddItemDTO) {
        String res =  cartService.updateItem(cartItemId,cartAddItemDTO);
        if (res.equals("修改数量成功")){
            return Response.buildSuccess(res);
        }else {
            return Response.buildFailure(res,"400");
        }
    }

    @GetMapping()
    public Response<?> getList() {
        return Response.buildSuccess(cartService.getList());
    }

    @PostMapping("/checkout")
    public Response<?> checkout(@RequestBody CartCheckOutInputDTO cartCheckOutInputDTO) {
        CartCheckOutOutputVO res = cartService.checkout(cartCheckOutInputDTO);
        if (res != null) {
            return Response.buildSuccess(res);
        }else {
            return Response.buildFailure("提交订单失败", "400");
        }
    }
}
