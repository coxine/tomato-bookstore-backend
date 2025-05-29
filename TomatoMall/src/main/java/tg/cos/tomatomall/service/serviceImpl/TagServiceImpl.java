package tg.cos.tomatomall.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.cos.tomatomall.dto.TagDTO;
import tg.cos.tomatomall.po.Tag;
import tg.cos.tomatomall.repository.TagRepository;
import tg.cos.tomatomall.service.TagService;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.TagVO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final SecurityUtil securityUtil;    
    @Transactional
    public List<TagVO> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        
        // 找出没有绑定商品的标签并删除
        List<Tag> tagsToDelete = tags.stream()
                .filter(tag -> tag.getProducts().isEmpty())
                .collect(Collectors.toList());
        
        if (!tagsToDelete.isEmpty()) {
            tagRepository.deleteAll(tagsToDelete);
            // 重新获取删除后的标签列表
            tags = tagRepository.findAll();
        }
        
        return tags.stream()
                .map(this::convertToTagVO)
                .collect(Collectors.toList());
    }

    @Override
    public TagVO getTagById(Integer id) {
        return tagRepository.findById(id)
                .map(this::convertToTagVO)
                .orElse(null);
    }

    @Override
    @Transactional
    public String createTag(TagDTO tagDTO) {
        // 检查权限
        if (!securityUtil.getCurrentUser().getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }

        // 检查标签名是否已存在
        if (tagRepository.findByName(tagDTO.getName()).isPresent()) {
            return "标签名已存在";
        }

        // 创建新标签
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        tagRepository.save(tag);

        return "创建成功";
    }

    @Override
    @Transactional
    public String updateTag(TagDTO tagDTO) {
        // 检查权限
        if (!securityUtil.getCurrentUser().getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }

        // 检查标签是否存在
        Optional<Tag> tagOptional = tagRepository.findById(tagDTO.getId());
        if (tagOptional.isEmpty()) {
            return "标签不存在";
        }

        // 检查新标签名是否与其他标签重复
        Optional<Tag> existingTag = tagRepository.findByName(tagDTO.getName());
        if (existingTag.isPresent() && !existingTag.get().getId().equals(tagDTO.getId())) {
            return "标签名已存在";
        }

        // 更新标签
        Tag tag = tagOptional.get();
        tag.setName(tagDTO.getName());
        tagRepository.save(tag);

        return "更新成功";
    }

    @Override
    @Transactional
    public String deleteTag(Integer id) {
        // 检查权限
        if (!securityUtil.getCurrentUser().getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }

        // 检查标签是否存在
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isEmpty()) {
            return "标签不存在";
        }

        // 检查标签是否被商品使用
        Tag tag = tagOptional.get();
        if (!tag.getProducts().isEmpty()) {
            return "标签正在被商品使用，无法删除";
        }

        // 删除标签
        tagRepository.delete(tag);

        return "删除成功";
    }

    private TagVO convertToTagVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }
} 