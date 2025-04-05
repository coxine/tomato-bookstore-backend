package tg.cos.tomatomall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tg.cos.tomatomall.service.PictureService;
import tg.cos.tomatomall.vo.Response;

@RestController
@RequestMapping("/api/picture")
public class PictureController {

    @Autowired
    private PictureService pictureService;

    @PostMapping("/account")
    public Response<?> uploadAccountAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        String res = pictureService.uploadAccountAvatar(file);
        if (res.equals("头像更新时间小于一周,请耐心等待")){
            return Response.buildFailure(res,"400");
        }
        return Response.buildSuccess(res);
    }
}
