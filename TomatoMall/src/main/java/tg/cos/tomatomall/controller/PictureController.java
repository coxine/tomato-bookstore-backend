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

    @PostMapping("/images")
    public Response<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return Response.buildSuccess(pictureService.upload(file));
    }
}
