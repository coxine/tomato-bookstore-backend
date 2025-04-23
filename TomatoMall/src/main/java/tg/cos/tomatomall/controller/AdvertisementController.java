package tg.cos.tomatomall.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tg.cos.tomatomall.service.AdvertisementService;
import tg.cos.tomatomall.vo.AdvertisementVO;
import tg.cos.tomatomall.vo.Response;

import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping
    public Response<?> getAllAdvertisements() {
        List<AdvertisementVO> res = advertisementService.getAll();
        if (res == null){
            return Response.buildFailure("获取失败", "400");
        }
        return Response.buildSuccess(res);
    }

    @PutMapping
    public Response<?> updateAdvertisement(@RequestBody AdvertisementVO vo) {
        String result = advertisementService.update(vo);
        if (result.equals("更新成功")){
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result, "400");
    }

    @PostMapping
    public Response<?> addAdvertisement(@RequestBody AdvertisementVO vo) {
        AdvertisementVO res = advertisementService.create(vo);
        if (res == null){
            return Response.buildFailure("商品不存在", "400");
        }
        return Response.buildSuccess(res);
    }

    @DeleteMapping("/{id}")
    public Response<?> deleteAdvertisement(@PathVariable("id") Integer id) {
        String result = advertisementService.delete(id);
        if (result.equals("删除成功")){
            return Response.buildSuccess(result);
        }
        return Response.buildFailure(result, "400");
    }

    @GetMapping("/{id}")
    public Response<?> getAdvertisementById(@PathVariable("id") Integer id) {
        AdvertisementVO res = advertisementService.getById(id);
        if (res == null) {
            return Response.buildFailure("广告不存在", "400");
        }
        return Response.buildSuccess(res);
    }
    
}
