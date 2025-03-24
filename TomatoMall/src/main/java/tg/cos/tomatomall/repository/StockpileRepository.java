package tg.cos.tomatomall.repository;

import tg.cos.tomatomall.po.Stockpile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockpileRepository extends JpaRepository<Stockpile, Integer> {
    Optional<Stockpile> findByProductId(Integer productId);
    void deleteByProductId(Integer productId);
}