package tg.cos.tomatomall.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tg.cos.tomatomall.po.Advertisement;
import tg.cos.tomatomall.repository.ProductRepository;
import tg.cos.tomatomall.vo.AdvertisementVO;

@Getter
@Setter
@NoArgsConstructor
public class AdvertisementDTO {
    Integer id;
    String title;
    String content;
    String imgUrl;
    Integer productId;

    public Advertisement toPO(){//set product on your own
        Advertisement advertisement=new Advertisement();
        if(id!=null)
            advertisement.setId(id);
        advertisement.setTitle(title);
        advertisement.setContent(content);
        advertisement.setImageUrl(imgUrl);
        return advertisement;
    }
    public AdvertisementVO toVO(){
        AdvertisementVO advertisementVO=new AdvertisementVO();
        if(id!=null)
            advertisementVO.setId(id);
        advertisementVO.setTitle(title);
        advertisementVO.setContent(content);
        advertisementVO.setImgUrl(imgUrl);
        if(productId!=null)
            advertisementVO.setProductId(productId);
        return advertisementVO;
    }
}
