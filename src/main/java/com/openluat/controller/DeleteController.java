package com.openluat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteController {
    @DeleteMapping("/")
    public ResponseEntity<String> deleteTest(@RequestBody String data) {
        if (data.equals("deleteTest")) {
            return new ResponseEntity<>("deleteTestSuccess", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("deleteTestFail", HttpStatus.BAD_REQUEST);
        }
    }
}
