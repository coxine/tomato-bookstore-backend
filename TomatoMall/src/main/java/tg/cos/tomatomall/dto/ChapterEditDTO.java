package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterEditDTO {
    private Integer productId;
    private Integer chapterId;
    private String name;
    private String content;
    private Integer previous;
    private Integer next;
    private String status;

}
