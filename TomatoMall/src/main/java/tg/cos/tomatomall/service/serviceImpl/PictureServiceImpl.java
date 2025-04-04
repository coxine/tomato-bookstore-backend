package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tg.cos.tomatomall.exception.TomatoMallException;
import tg.cos.tomatomall.service.PictureService;
import tg.cos.tomatomall.util.OSSUtil;
import tg.cos.tomatomall.vo.Response;

@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    private OSSUtil ossUtil;

    public String upload(MultipartFile file) throws Exception {
        try {
//            System.out.println(file.getOriginalFilename());
//            System.out.println(file.getContentType());
            return ossUtil.upload(file.getOriginalFilename(),file.getInputStream());
        }catch (Exception e){
            e.printStackTrace();
            throw TomatoMallException.fileUploadFail();
        }
    }
}
