package tg.cos.tomatomall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tg.cos.tomatomall.dto.TagDTO;
import tg.cos.tomatomall.service.TagService;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.Response;
import tg.cos.tomatomall.vo.TagVO;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     */
    @GetMapping
    public Response<?> getAllTags() {
        List<TagVO> tags = tagService.getAllTags();
        return Response.buildSuccess(tags);
    }

    /**
     * 获取指定标签
     */
    @GetMapping("/{id}")
    public Response<?> getTagById(@PathVariable("id") Integer id) {
        TagVO tag = tagService.getTagById(id);
        if (tag != null) {
            return Response.buildSuccess(tag);
        }
        return Response.buildFailure("标签不存在", "400");
    }

    /**
     * 创建标签
     */
    @PostMapping
    public Response<?> createTag(@RequestBody TagDTO tagDTO) {
        if (!securityUtil.getCurrentUser().getRole().toUpperCase().equals("ADMIN")) {
            return Response.buildFailure("无权限访问", "400");
        }
        String result = tagService.createTag(tagDTO);
        if (result.equals("创建成功")) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result, "400");
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public Response<?> updateTag(@PathVariable("id") Integer id, @RequestBody TagDTO tagDTO) {
        tagDTO.setId(id);
        String result = tagService.updateTag(tagDTO);
        if (result.equals("更新成功")) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result, "400");
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public Response<?> deleteTag(@PathVariable("id") Integer id) {
        if (!securityUtil.getCurrentUser().getRole().toUpperCase().equals("ADMIN")) {
            return Response.buildFailure("无权限访问", "400");
        }
        String result = tagService.deleteTag(id);
        if (result.equals("删除成功")) {
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result, "400");
    }
} 