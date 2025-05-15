package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;
import tg.cos.tomatomall.vo.CartCheckOutShippingAddress;

import java.util.List;

@Getter
@Setter
public class ChapterCheckoutDTO {
    CartCheckOutShippingAddress shipping_address;
    List<Integer> chapters;
    String payment_method;
}
