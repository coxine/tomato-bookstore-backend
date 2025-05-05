package tg.cos.tomatomall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.cos.tomatomall.po.Order;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "orderItems.product.stockpile"})
    List<Order> findByStatusAndCreateTimeBefore(String status, Date createTime);
    
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByAccountId(Integer accountId);
}
