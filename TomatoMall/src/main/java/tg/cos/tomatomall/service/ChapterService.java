package tg.cos.tomatomall.service;

import tg.cos.tomatomall.dto.ChapterCheckoutDTO;
import tg.cos.tomatomall.dto.ChapterEditDTO;
import tg.cos.tomatomall.vo.CartCheckOutOutputVO;
import tg.cos.tomatomall.vo.ChapterGetAllVO;
import tg.cos.tomatomall.vo.ChapterGetVO;

import java.io.IOException;
import java.util.List;

public interface ChapterService {
    String addChapter(ChapterEditDTO chapter) throws IOException;
    ChapterGetVO getChapter(Integer id);
    String updateChapter(ChapterEditDTO chapter) throws IOException;
    String deleteChapter(Integer id);
    List<ChapterGetAllVO> getAllChapter(Integer id);
    CartCheckOutOutputVO checkout(Integer productId, ChapterCheckoutDTO chapterCheckoutDTO);
    int[] findChaptersBought(Integer productId, Integer accountId);
    int[] findChaptersBought(Integer productId);
}
