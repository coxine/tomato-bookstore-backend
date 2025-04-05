package tg.cos.tomatomall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.cos.tomatomall.po.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
