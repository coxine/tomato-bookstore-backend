package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tg.cos.tomatomall.exception.TomatoMallException;
import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.service.PictureService;
import tg.cos.tomatomall.util.OSSUtil;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.Response;

import java.util.Date;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    private OSSUtil ossUtil;
    @Autowired
    SecurityUtil securityUtil;

    public String uploadAccountAvatar(MultipartFile file) throws Exception {
        try {
//            System.out.println(file.getOriginalFilename());
//            System.out.println(file.getContentType());
            Account account=securityUtil.getCurrentUser();
            Date date=new Date();
            if (account.getLatestAvatarChangeTime() != null && date.getTime() - account.getLatestAvatarChangeTime().getTime() < 604800000){
                return "头像更新时间小于一周,请耐心等待";
            }
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String accountAvatarFileName = UUID.randomUUID().toString() + extension;

            return ossUtil.upload(accountAvatarFileName,file.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
            throw TomatoMallException.fileUploadFail();
        }
    }
}
