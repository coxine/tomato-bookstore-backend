package tg.cos.tomatomall.dto;

import lombok.Data;
import tg.cos.tomatomall.vo.CartCheckOutShippingAddress;

import java.util.List;
@Data
public class CartCheckOutInputDTO {
    List<Integer> cartItemIds;
    CartCheckOutShippingAddress shipping_address;
    String payment_method;
}
