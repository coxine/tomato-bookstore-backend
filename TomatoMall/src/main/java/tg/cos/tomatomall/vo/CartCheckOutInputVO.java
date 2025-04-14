package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartCheckOutInputVO {
    List<Integer> cartItemIds;
    CartCheckOutShippingAddress shipping_address;
    String payment_method;
}
