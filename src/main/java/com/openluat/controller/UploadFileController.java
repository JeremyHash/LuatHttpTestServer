package com.openluat.controller;

import com.openluat.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/*
 * 文件上传Controller
 * */
@Slf4j
@RestController
public class UploadFileController {

    @Autowired
    private Utils utils;

    @PostMapping("/uploadFile")
    public ResponseEntity<String> handleFileUpload(@RequestParam("imei") String imei, @RequestParam("time") String time, @RequestParam("md5") String md5, @RequestParam("FormDataUploadFile") MultipartFile file) {
        log.info("imei:" + imei);
        log.info("time:" + time);
        log.info("md5 = " + md5);
        File localFile;
        String calMD5;
        if (!file.isEmpty()) {
            try {
                localFile = new File(file.getOriginalFilename());
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
                out.write(file.getBytes());
                out.flush();
                out.close();
                calMD5 = utils.getMD5(localFile);
                log.info("calMD5 = " + calMD5);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("上传失败," + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (calMD5.equals(md5)) {
                return new ResponseEntity<>("postTestWithMultipartFormDataSuccess", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("postTestWithMultipartFormDataFail", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("上传失败，因为文件是空的", HttpStatus.BAD_REQUEST);
        }
    }
}
