package tg.cos.tomatomall.service;

import org.springframework.web.multipart.MultipartFile;

public interface PictureService {
    String uploadAccountAvatar(MultipartFile file) throws Exception;
    String uploadProductCover(MultipartFile file, Integer id) throws Exception;
    String uploadProductCoverCreate(MultipartFile file) throws Exception;
    String uploadAdvertisementCover(MultipartFile file, Integer id) throws Exception;
    String uploadAdvertisementCoverCreate(MultipartFile file) throws Exception;
}
