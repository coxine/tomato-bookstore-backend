package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.po.Advertisement;
import tg.cos.tomatomall.po.Product;
import tg.cos.tomatomall.repository.AdvertisementRepository;
import tg.cos.tomatomall.repository.ProductRepository;
import tg.cos.tomatomall.service.AdvertisementService;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.AdvertisementVO;

import java.util.*;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    AdvertisementRepository advertisementRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    SecurityUtil securityUtil;

    @Override
    public List<AdvertisementVO> getAll(){
        List<Advertisement> advertisements = advertisementRepository.findAll();
        if (advertisements.isEmpty()){
            return Collections.emptyList();
        }
        List<AdvertisementVO> res = new ArrayList<>();
        for (Advertisement advertisement : advertisements){
            AdvertisementVO vo = new AdvertisementVO();
            vo.setId(advertisement.getId());
            vo.setTitle(advertisement.getTitle());
            vo.setContent(advertisement.getContent());
            vo.setImgUrl(advertisement.getImageUrl());
            vo.setProductId(advertisement.getProduct().getId());
            res.add(vo);
        }
        return res;
    }

    @Override
    public String update(AdvertisementVO advertisementVO) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return "需要管理员权限";
        }
        Optional<Advertisement> advertisementOptional = advertisementRepository.findById(advertisementVO.getId());
        if (advertisementOptional.isEmpty()){
            return "商品不存在";
        }
        Advertisement advertisement = advertisementOptional.get();
        if (advertisementVO.getTitle() != null){
            advertisement.setTitle(advertisementVO.getTitle());
        }
        if (advertisementVO.getContent() != null){
            advertisement.setContent(advertisementVO.getContent());
        }
        if (advertisementVO.getImgUrl() != null){
            advertisement.setImageUrl(advertisementVO.getImgUrl());
            advertisement.setLastImageChangeTime(new Date());
        }
        if (advertisementVO.getProductId() != null){
            Optional<Product> productOptional = productRepository.findById(advertisementVO.getProductId());
            if (productOptional.isEmpty()){
                return "商品不存在";
            }
            Product product = productOptional.get();
            advertisement.setProduct(product);
        }
        advertisementRepository.save(advertisement);
        return "更新成功";
    }

    @Override
    public AdvertisementVO create(AdvertisementVO advertisementVO) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return null;
        }
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(advertisementVO.getTitle());
        advertisement.setContent(advertisementVO.getContent());
        advertisement.setImageUrl(advertisementVO.getImgUrl());
        advertisement.setLastImageChangeTime(new Date());
        if (advertisementVO.getImgUrl().startsWith("https") && !account.getUploadAdvertisementCoverCreates().isEmpty()) {
            account.getUploadAdvertisementCoverCreates().removeLast();
        }
        Optional<Product> productOptional = productRepository.findById(advertisementVO.getProductId());
        if (productOptional.isEmpty()){
            return null;
        }
        Product product = productOptional.get();
        advertisement.setProduct(product);
        advertisementRepository.save(advertisement);
        AdvertisementVO vo = new AdvertisementVO();
        vo.setId(advertisement.getId());
        vo.setTitle(advertisement.getTitle());
        vo.setContent(advertisement.getContent());
        vo.setImgUrl(advertisement.getImageUrl());
        vo.setProductId(advertisement.getProduct().getId());
        return vo;
    }

    @Override
    public String delete(Integer id) {
        Account account = securityUtil.getCurrentUser();
        if (!account.getRole().toUpperCase().equals("ADMIN")) {
            return null;
        }
        Optional<Advertisement> advertisementOptional = advertisementRepository.findById(id);
        if (advertisementOptional.isEmpty()){
            return "该商品不存在";
        }
        advertisementRepository.deleteById(id);
        return "删除成功";
    }

    @Override
public AdvertisementVO getById(Integer id) {
    Optional<Advertisement> advertisementOptional = advertisementRepository.findById(id);
    if (advertisementOptional.isEmpty()) {
        return null;
    }
    Advertisement advertisement = advertisementOptional.get();
    AdvertisementVO vo = new AdvertisementVO();
    vo.setId(advertisement.getId());
    vo.setTitle(advertisement.getTitle());
    vo.setContent(advertisement.getContent());
    vo.setImgUrl(advertisement.getImageUrl());
    vo.setProductId(advertisement.getProduct().getId());
    return vo;
}
}
