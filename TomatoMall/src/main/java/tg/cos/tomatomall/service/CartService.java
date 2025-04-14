package tg.cos.tomatomall.service;

import tg.cos.tomatomall.vo.CartAddItemVO;
import tg.cos.tomatomall.vo.CartCheckOutInputVO;
import tg.cos.tomatomall.vo.CartCheckOutOutputVO;
import tg.cos.tomatomall.vo.CartGetListVO;

public interface CartService {
    CartAddItemVO addItem(CartAddItemVO cartAddItemVO);
    String deleteItem(Integer id);
    String updateItem(Integer id,CartAddItemVO cartAddItemVO);
    CartGetListVO getList();
    CartCheckOutOutputVO checkout(CartCheckOutInputVO cartCheckOutInputVO);
}
