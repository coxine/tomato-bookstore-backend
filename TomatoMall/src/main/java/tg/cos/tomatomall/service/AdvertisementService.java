package tg.cos.tomatomall.service;

import tg.cos.tomatomall.dto.AdvertisementDTO;
import tg.cos.tomatomall.vo.AdvertisementVO;

import java.util.List;

public interface AdvertisementService {
    List<AdvertisementVO> getAll();
    String update(AdvertisementDTO advertisementDTO);
    AdvertisementVO create(AdvertisementDTO advertisementDTO);
    String delete(Integer id);
    AdvertisementVO getById(Integer id);
}
