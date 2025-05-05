package tg.cos.tomatomall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.cos.tomatomall.po.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
}
