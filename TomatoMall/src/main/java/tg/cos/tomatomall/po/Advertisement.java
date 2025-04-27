package tg.cos.tomatomall.po;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tg.cos.tomatomall.dto.AdvertisementDTO;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "advertisements")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private int id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "FK_PRODUCT_ADVERTISEMENT"))
    private Product product;

    @Column(name = "last_image_change_time")
    private Date lastImageChangeTime;
    public AdvertisementDTO toDTO(){
        AdvertisementDTO advertisementDTO=new AdvertisementDTO();
        advertisementDTO.setId(id);
        advertisementDTO.setTitle(title);
        advertisementDTO.setContent(content);
        advertisementDTO.setImgUrl(imageUrl);
        advertisementDTO.setProductId(this.product.getId());
        return advertisementDTO;
    }

}
