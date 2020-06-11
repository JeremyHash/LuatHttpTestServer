package com.openluat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PostMapping(path = "/getContentLength")
    public int postTest(@RequestBody String data) {
        return data.length();
    }

    @GetMapping(path = "/")
    public String getPost() {
        return "JeremyHttpServerGetTest";
    }
}
