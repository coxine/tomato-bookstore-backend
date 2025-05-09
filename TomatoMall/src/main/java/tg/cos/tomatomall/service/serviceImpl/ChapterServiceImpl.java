package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tg.cos.tomatomall.dto.ChapterEditDTO;
import tg.cos.tomatomall.po.Chapter;
import tg.cos.tomatomall.po.Product;
import tg.cos.tomatomall.repository.ChapterRepository;
import tg.cos.tomatomall.repository.ProductRepository;
import tg.cos.tomatomall.service.ChapterService;
import tg.cos.tomatomall.util.OSSUtil;
import tg.cos.tomatomall.vo.ChapterGetAllVO;
import tg.cos.tomatomall.vo.ChapterGetVO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.mock.web.MockMultipartFile;

@Service
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OSSUtil ossUtil;

    @Override
    public String addChapter(ChapterEditDTO chapter) throws IOException {
        Optional<Product> productOptional = productRepository.findById(chapter.getProductId());
        if (productOptional.isEmpty()) {
            return null;
        }
        Product product = productOptional.get();

        Chapter chapterEntity = new Chapter();
        chapterEntity.setName(chapter.getName());
        chapterEntity.setContent(uploadFile(convertStringToMultipartFile(chapter.getContent())));
        chapterEntity.setProduct(product);
        chapterEntity.setStatus(chapter.getStatus());

        if (chapter.getPrevious() != null) {
            chapterEntity.setPrevious(chapter.getPrevious());
        }else {
            Chapter last = product.getChapters().getLast();
            int lastId = last.getId();
            chapterEntity.setPrevious(lastId);
            last.setNext(chapterEntity.getId());
        }
        if (chapter.getNext() != null) {
            chapterEntity.setNext(chapter.getNext());
        }
        product.getChapters().add(chapterEntity);
        productRepository.save(product);
        chapterRepository.save(chapterEntity);
        return "录入章节成功";
    }

    @Override
    public ChapterGetVO getChapter(Integer id) {
        Optional<Chapter> chapterOptional = chapterRepository.findById(id);
        if (chapterOptional.isEmpty()) {
            return null;
        }
        Chapter chapter = chapterOptional.get();
        ChapterGetVO chapterGetVO = new ChapterGetVO();
        chapterGetVO.setId(chapter.getId());
        chapterGetVO.setName(chapter.getName());
        if (chapter.getPrevious() != null) {
            chapterGetVO.setPrevious(chapter.getPrevious());
        }
        if (chapter.getNext() != null) {
            chapterGetVO.setNext(chapter.getNext());
        }
        chapterGetVO.setProductId(chapter.getProduct().getId());
        chapterGetVO.setStatus(chapter.getStatus());
        chapterGetVO.setContent(getFile(chapter.getContent()));

        return chapterGetVO;
    }

    @Override
    public String updateChapter(ChapterEditDTO chapter) throws IOException {
        Optional<Chapter> chapterOptional = chapterRepository.findById(chapter.getChapterId());
        if (chapterOptional.isEmpty()) {
            return "查找不到该章节";
        }
        Chapter chapterEntity = chapterOptional.get();

        if (chapter.getName() != null) {
            chapterEntity.setName(chapter.getName());
        }
        if (chapter.getContent() != null) {
            chapterEntity.setContent(uploadFile(convertStringToMultipartFile(chapter.getContent())));
        }
        if (chapter.getPrevious() != null) {
            chapterEntity.setPrevious(chapter.getPrevious());
        }
        if (chapter.getNext() != null) {
            chapterEntity.setNext(chapter.getNext());
        }
        if (chapter.getStatus() != null) {
            chapterEntity.setStatus(chapter.getStatus());
        }
        chapterRepository.save(chapterEntity);

        return "更新章节成功";
    }

    @Override
    public String deleteChapter(Integer id) {
        Optional<Chapter> chapterOptional = chapterRepository.findById(id);
        if (chapterOptional.isEmpty()) {
            return "查找不到该章节";
        }
        Chapter chapter = chapterOptional.get();
        chapterRepository.delete(chapter);
        return "删除章节成功";
    }

    @Override
    public List<ChapterGetAllVO> getAllChapter(Integer id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return null;
        }
        Product product = productOptional.get();
        List<ChapterGetAllVO> chapterGetAllVOList = new ArrayList<>();
        for (Chapter chapter : product.getChapters()) {
            ChapterGetAllVO chapterGetAllVO = new ChapterGetAllVO();
            chapterGetAllVO.setId(chapter.getId());
            chapterGetAllVO.setName(chapter.getName());
            if (chapter.getPrevious() != null) {
                chapterGetAllVO.setPrevious(chapter.getPrevious());
            }
            if (chapter.getNext() != null) {
                chapterGetAllVO.setNext(chapter.getNext());
            }
            chapterGetAllVO.setStatus(chapter.getStatus());
            chapterGetAllVO.setProductId(product.getId());
            chapterGetAllVOList.add(chapterGetAllVO);
        }
        return chapterGetAllVOList;
    }

    private MultipartFile convertStringToMultipartFile(String text) {
        byte[] content = text.getBytes(StandardCharsets.UTF_8);
        String fileName = "chapter"+UUID.randomUUID().toString().replace("-", "");

        // 创建MockMultipartFile对象
        return new MockMultipartFile(
                fileName,          // 文件名
                fileName,          // 原始文件名
                "text/plain",      // 内容类型
                content            // 内容字节数组
        );
    }

    private String convertMultipartFileToString(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    private String uploadFile(MultipartFile file) throws IOException {
        return ossUtil.upload(file.getName(),file.getInputStream()).replace("http:", "https:");
    }

    public static String getFile(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            StringBuilder content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
