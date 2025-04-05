package tg.cos.tomatomall.controller;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;

import tg.cos.tomatomall.vo.Response;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class HomeController {

    @Value("${message}")
    private String message;

    @GetMapping
    public String home() {
        return message;
    }

    @PostMapping
    public Response<JsonNode> testPost(@RequestBody JsonNode body) {
        JsonNode data = body.get("data");
        return Response.buildSuccess(data);
    }
}
