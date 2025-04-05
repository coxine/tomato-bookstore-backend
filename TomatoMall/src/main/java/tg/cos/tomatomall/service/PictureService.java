package tg.cos.tomatomall.service;

import org.springframework.web.multipart.MultipartFile;

public interface PictureService {
    String uploadAccountAvatar(MultipartFile file) throws Exception;
}
