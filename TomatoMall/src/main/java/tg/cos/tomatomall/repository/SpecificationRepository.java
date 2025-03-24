package tg.cos.tomatomall.repository;

import tg.cos.tomatomall.po.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificationRepository extends JpaRepository<Specification, Integer> {
    List<Specification> findByProductId(Integer productId);
    void deleteByProductId(Integer productId);
}