package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.po.CartItem;
import tg.cos.tomatomall.po.Product;
import tg.cos.tomatomall.repository.AccountRepository;
import tg.cos.tomatomall.repository.CartItemRepository;
import tg.cos.tomatomall.repository.OrderRepository;
import tg.cos.tomatomall.repository.ProductRepository;
import tg.cos.tomatomall.service.CartService;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.CartAddItemVO;
import tg.cos.tomatomall.vo.CartGetListVO;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public CartAddItemVO addItem(CartAddItemVO cartAddItemVO){
        CartAddItemVO result = new CartAddItemVO();
        Account account = securityUtil.getCurrentUser();
        int productId = cartAddItemVO.getProductId();
        Optional<Product> productOptional = productRepository.findById(productId);
        Product product;
        if(productOptional.isPresent()){
            product = productOptional.get();
        }else {
            return null;
        }
        int quantity = cartAddItemVO.getQuantity();
        if (quantity < product.getStockpile().getAmount()){
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setAccount(account);
        cartItemRepository.save(cartItem);

        account.getCartItems().add(cartItem);
        accountRepository.save(account);

        result.setProductId(productId);
        result.setCartItemId(cartItem.getId());
        result.setTitle(product.getTitle());
        result.setPrice(product.getPrice());
        String cover = product.getCover();
        if (cover != null){
            result.setCover(cover);
        }
        String description = product.getDescription();
        if (description != null){
            result.setDescription(description);
        }
        String detail = product.getDetail();
        if (detail != null){
            result.setDetail(detail);
        }
        result.setQuantity(quantity);
        return result;
    }

    @Override
    public String deleteItem(Integer id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isEmpty()){
            return "购物车商品不存在";
        }

        cartItemRepository.deleteById(id);
        return "删除成功";
    }

    @Override
    public String updateItem(Integer id, CartAddItemVO cartAddItemVO) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isEmpty()){
            return "购物车商品不存在";
        }
        CartItem cartItem = cartItemOptional.get();
        cartItem.setQuantity(cartAddItemVO.getQuantity());
        if (cartItem.getQuantity() > cartItemOptional.get().getProduct().getStockpile().getAmount()){
            return "购物车商品不存在";
        }
        cartItemRepository.save(cartItem);
        return "修改数量成功";
    }

    @Override
    public CartGetListVO getList(){
        Account account = securityUtil.getCurrentUser();
        Set<CartItem> cartItems = account.getCartItems();

        CartGetListVO result = new CartGetListVO();

        int total = 0;
        BigDecimal amount = new BigDecimal(0);
        Set<CartAddItemVO> cartAddItemVOSet = new HashSet<>();

        for (CartItem cartItem : cartItems){
            total++;
            CartAddItemVO addItemVO = new CartAddItemVO();
            addItemVO.setCartItemId(cartItem.getId());
            addItemVO.setProductId(cartItem.getProduct().getId());
            addItemVO.setTitle(cartItem.getProduct().getTitle());
            BigDecimal price = cartItem.getProduct().getPrice();
            addItemVO.setPrice(price);
            String description = cartItem.getProduct().getDescription();
            if (description != null){
                addItemVO.setDescription(description);
            }
            String detail = cartItem.getProduct().getDetail();
            if (detail != null){
                addItemVO.setDetail(detail);
            }
            String cover = cartItem.getProduct().getCover();
            if (cover != null){
                addItemVO.setCover(cover);
            }
            int quantity = cartItem.getQuantity();
            addItemVO.setQuantity(quantity);
            cartAddItemVOSet.add(addItemVO);
            BigDecimal DecimalQuantity = new BigDecimal(quantity);
            amount = amount.add(DecimalQuantity.multiply(price));
        }
        result.setTotal(total);
        result.setItems(cartAddItemVOSet);
        result.setTotalAmount(amount);
        return result;
    }
}
