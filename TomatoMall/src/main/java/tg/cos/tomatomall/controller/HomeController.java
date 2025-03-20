package tg.cos.tomatomall.controller;

import com.fasterxml.jackson.databind.JsonNode;

import tg.cos.tomatomall.vo.Response;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String home() {
        return "Welcome to Tomato Mall!\nHere is the backend of this application!";
    }

    @PostMapping
    public Response<JsonNode> testPost(@RequestBody JsonNode body) {
        JsonNode data = body.get("data");
        return Response.buildSuccess(data);
    }
}
