package com.openluat.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/test")
public class TestController {

    @GetMapping(path = "/")
    public String rootPathHandler() {
        return "JeremyHttpServerGetTest";
    }

    @PostMapping(path = "/getContentLength")
    public int getContentLength(@RequestBody String data) {
        return data.length();
    }

}
