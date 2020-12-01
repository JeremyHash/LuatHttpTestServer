package com.openluat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PutController {
    @PutMapping("/")
    public ResponseEntity<String> putTest(@RequestBody String data) {
        if (data.equals("putTest")) {
            return new ResponseEntity<>("putTestSuccess", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("putTestFail", HttpStatus.BAD_REQUEST);
        }
    }
}
