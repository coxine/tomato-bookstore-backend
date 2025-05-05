package tg.cos.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChapterGetVO {
    private int id;
    private int productId;
    private String name;
    private int previous;
    private int next;
    private String status;
    private String content;
}
