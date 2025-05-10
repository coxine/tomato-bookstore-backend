package tg.cos.tomatomall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tg.cos.tomatomall.dto.ChapterEditDTO;
import tg.cos.tomatomall.service.ChapterService;
import tg.cos.tomatomall.vo.ChapterGetAllVO;
import tg.cos.tomatomall.vo.ChapterGetVO;
import tg.cos.tomatomall.vo.Response;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;

    @PostMapping("/{productId}/chapters")
    public Response<?> addChapter(@PathVariable("productId") Integer productId, @RequestBody ChapterEditDTO chapter) throws IOException {
        chapter.setProductId(productId);
        String result = chapterService.addChapter(chapter);
        if (result.equals("录入章节成功")) {
            return Response.buildSuccess(result);
        }else {
            return Response.buildFailure(result,"400");
        }
    }

    @PutMapping("/chapters/{chapterId}")
    public Response<?> updateChapter(@PathVariable("chapterId") Integer chapterId, @RequestBody ChapterEditDTO chapter) throws IOException {
        chapter.setChapterId(chapterId);
        String result = chapterService.updateChapter(chapter);
        if (result.equals("查找不到章节")){
            return Response.buildFailure(result,"400");
        }else if (result.equals("更新章节成功")){
            return Response.buildSuccess(result);
        }else {
            return Response.buildFailure(result,"400");
        }
    }

    @DeleteMapping("/chapters/{chapterId}")
    public Response<?> deleteChapter(@PathVariable("chapterId") Integer chapterId) throws IOException {
        String result = chapterService.deleteChapter(chapterId);
        if (result.equals("删除章节成功")){
            return Response.buildSuccess(result);
        }else if (result.equals("查找不到章节")){
            return Response.buildFailure(result,"400");
        }else {
            return Response.buildFailure("删除章节失败","400");
        }

    }

    @GetMapping("/{productId}/chapters")
    public Response<?> getAllChapters(@PathVariable("productId") Integer productId) throws IOException {
        List<ChapterGetAllVO> chapterGetAllVO = chapterService.getAllChapter(productId);
        if (chapterGetAllVO != null){
            return Response.buildSuccess(chapterGetAllVO);
        }else {
            return Response.buildFailure("获取全部章节失败","400");
        }
    }

    @GetMapping("/chapters/{chapterId}")
    public Response<?> getChapter(@PathVariable("chapterId") Integer chapterId) throws IOException {
        ChapterGetVO chapterGetVO = chapterService.getChapter(chapterId);
        if (chapterGetVO != null){
            return Response.buildSuccess(chapterGetVO);
        } else {
          return Response.buildFailure("获取章节失败","400");
        }
    }
}
