package tg.cos.tomatomall.service;

import tg.cos.tomatomall.vo.AdvertisementVO;

import java.util.List;

public interface AdvertisementService {
    List<AdvertisementVO> getAll();
    String update(AdvertisementVO advertisementVO);
    AdvertisementVO create(AdvertisementVO advertisementVO);
    String delete(Integer id);
    AdvertisementVO getById(Integer id);
}
