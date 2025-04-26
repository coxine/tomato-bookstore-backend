package tg.cos.tomatomall.service;

import tg.cos.tomatomall.dto.CartAddItemDTO;
import tg.cos.tomatomall.dto.CartCheckOutInputDTO;
import tg.cos.tomatomall.vo.CartAddItemVO;
import tg.cos.tomatomall.vo.CartCheckOutInputVO;
import tg.cos.tomatomall.vo.CartCheckOutOutputVO;
import tg.cos.tomatomall.vo.CartGetListVO;

public interface CartService {
    CartAddItemVO addItem(CartAddItemDTO cartAddItemDTO);
    String deleteItem(Integer id);
    String updateItem(Integer id,CartAddItemDTO cartAddItemDTO);
    CartGetListVO getList();
    CartCheckOutOutputVO checkout(CartCheckOutInputDTO cartCheckOutInputDTO);
}
