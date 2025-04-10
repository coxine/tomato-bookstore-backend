package tg.cos.tomatomall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/product/{productId}")
    public Response<?> uploadProductCover(@RequestParam("file") MultipartFile file,
                                          @PathVariable("productId") Integer productId) throws Exception {
        String res = pictureService.uploadProductCover(file, productId);
        if (!res.startsWith("https:")){
            return Response.buildFailure(res,"400");
        }
        return Response.buildSuccess(res);
    }

    @PostMapping("/product")
    public Response<?> uploadProductCoverCreate(@RequestParam("file") MultipartFile file) throws Exception {
        String res = pictureService.uploadProductCoverCreate(file);
        if (!res.startsWith("https:")){
            return Response.buildFailure(res,"400");
        }
        return Response.buildSuccess(res);
    }
}
