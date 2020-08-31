package com.openluat.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class GetController {

    @GetMapping(path = "/")
    public ResponseEntity<String> getTest() {
        return new ResponseEntity<>("LuatHttpTestServerGetTestOK", HttpStatus.OK);
    }


    @GetMapping(path = "/redirect301")
    public ResponseEntity<String> redirectHandler1() {
        HttpHeaders httpHeaders = new HttpHeaders();
        ArrayList<String> list = new ArrayList<>();
        list.add("http://www.openluat.com/Product/images/720dmod/1.png");
        httpHeaders.put("location", list);
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping(path = "/redirect302")
    public ResponseEntity<String> redirectHandler2() {
        HttpHeaders httpHeaders = new HttpHeaders();
        ArrayList<String> list = new ArrayList<>();
        list.add("https://www.baidu.com");
        httpHeaders.put("Location", list);
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }

}
