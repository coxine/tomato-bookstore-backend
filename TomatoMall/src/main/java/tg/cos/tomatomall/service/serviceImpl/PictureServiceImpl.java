package tg.cos.tomatomall.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tg.cos.tomatomall.exception.TomatoMallException;
import tg.cos.tomatomall.po.Account;
import tg.cos.tomatomall.po.Product;
import tg.cos.tomatomall.repository.AccountRepository;
import tg.cos.tomatomall.repository.ProductRepository;
import tg.cos.tomatomall.service.PictureService;
import tg.cos.tomatomall.util.OSSUtil;
import tg.cos.tomatomall.util.SecurityUtil;
import tg.cos.tomatomall.vo.Response;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    private OSSUtil ossUtil;
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    AccountRepository accountRepository;

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

            return ossUtil.upload(accountAvatarFileName,file.getInputStream()).replace("http:", "https:");
        }catch (Exception e){
            e.printStackTrace();
            throw TomatoMallException.fileUploadFail();
        }
    }

    @Override
    public String uploadProductCover(MultipartFile file, Integer id) throws Exception {
        try {
            Account account=securityUtil.getCurrentUser();
            Date date=new Date();
            if (!account.getRole().toUpperCase().equals("ADMIN")){
                return "需要管理员身份";
            }
            Optional<Product> productOptional=productRepository.findById(id);
            Product product;
            if (productOptional.isPresent()){
                product=productOptional.get();
            }else {
                return "不存在该商品";
            }
            if (product.getLastChangeCover() != null && date.getTime() - product.getLastChangeCover().getTime() < 86400000){
                return "封面更新时间小于一天,请耐心等待";
            }
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String accountAvatarFileName = UUID.randomUUID().toString() + extension;

            return ossUtil.upload(accountAvatarFileName,file.getInputStream()).replace("http:", "https:");
        }catch (Exception e){
            e.printStackTrace();
            throw TomatoMallException.fileUploadFail();
        }
    }

    @Override
    public String uploadProductCoverCreate(MultipartFile file) throws Exception {
        try {
            Account account=securityUtil.getCurrentUser();
            Date date=new Date();
            if (!account.getRole().toUpperCase().equals("ADMIN")){
                return "需要管理员身份";
            }
            for (Long time : account.getUploadProductCoverCreates()){
                if (date.getTime() - time > 604800000){
                    account.getUploadProductCoverCreates().remove(time);
                }
            }
            if (account.getUploadProductCoverCreates().size() >= 5){
                return "已经传入了太多图片!!!";
            }
            account.getUploadProductCoverCreates().add(date.getTime());
            accountRepository.save(account);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String accountAvatarFileName = UUID.randomUUID().toString() + extension;

            return ossUtil.upload(accountAvatarFileName,file.getInputStream()).replace("http:", "https:");
        }catch (Exception e){
            e.printStackTrace();
            throw TomatoMallException.fileUploadFail();
        }
    }
}
