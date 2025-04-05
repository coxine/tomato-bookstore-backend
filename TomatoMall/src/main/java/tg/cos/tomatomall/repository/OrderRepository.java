package tg.cos.tomatomall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.cos.tomatomall.po.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
}
