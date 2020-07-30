package com.openluat.controller;

import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/uploadFile")
    public String handleFileUpload(@RequestParam("imei") String imei, @RequestParam("time") String time, @RequestParam("FormDataUploadFile") MultipartFile file) {
        log.info("imei:" + imei);
        log.info("time:" + time);
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(Objects.requireNonNull(file.getOriginalFilename()))));
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            }

            return "上传成功";

        } else {
            return "上传失败，因为文件是空的";
        }
    }
}
