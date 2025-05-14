package tg.cos.tomatomall.service;

import tg.cos.tomatomall.dto.TagDTO;
import tg.cos.tomatomall.vo.TagVO;
import java.util.List;

public interface TagService {
    /**
     * 获取所有标签
     */
    List<TagVO> getAllTags();

    /**
     * 根据ID获取标签
     */
    TagVO getTagById(Integer id);

    /**
     * 创建标签
     */
    String createTag(TagDTO tagDTO);

    /**
     * 更新标签
     */
    String updateTag(TagDTO tagDTO);

    /**
     * 删除标签
     */
    String deleteTag(Integer id);
} 