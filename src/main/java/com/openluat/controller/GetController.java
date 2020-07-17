package com.openluat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetController {

    @GetMapping(path = "/")
    public ResponseEntity<String> getTest() {
        return new ResponseEntity<>("LuatHttpTestServerGetTestOK", HttpStatus.OK);
    }
}
