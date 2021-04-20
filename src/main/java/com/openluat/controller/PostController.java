package com.openluat.controller;

import com.alibaba.fastjson.JSONObject;
import com.openluat.pojo.LbsLocInfo;
import com.openluat.pojo.TestJson;
import com.openluat.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@RestController
public class PostController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private Utils utils;

    @PostMapping(path = "/")
    public ResponseEntity<String> postTest(@RequestBody String data) {
        if (data.equals("PostTest")) {
            return ResponseEntity.ok("postTestSuccess");
        } else {
            return new ResponseEntity<>("postTestFail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/calcPostDataLen")
    public ResponseEntity<Integer> calcPostDataLen(@RequestBody String data) {
        return ResponseEntity.ok(data.length());
    }

    @PostMapping(path = "/postJsonTest")
    public ResponseEntity<String> postJsonTest(@RequestBody TestJson testJson) {
        if (testJson.getImei().equals("123456789012345") && testJson.getMcc().equals("460") && testJson.getMnc().equals("0") && testJson.getLac().equals("21133") && testJson.getCi().equals("52365") && testJson.getHex().equals("10")) {
            return new ResponseEntity<>("postJsonTestSuccess", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("postJsonTestFail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/getContentLength")
    public ResponseEntity<Integer> getContentLength(@RequestBody String data) {
        return ResponseEntity.ok(data.length());
    }

    @PostMapping(path = "/withUserHead")
    public ResponseEntity<String> userHeadTest(@RequestHeader("UserHead") String userHead, @RequestHeader("User-Agent") String userAgent, @RequestHeader("Cookie") String cookie, @RequestHeader("Authorization") String authorization) {
        if (userHead.equals("PostTestWithUserHead") && userAgent.equals("AirM2M") && cookie.equals("1234567890asdfghjklp".repeat(50)) && authorization.equals("Basic " + new String(Base64Utils.encode("123:456".getBytes())))) {
            return new ResponseEntity<>("PostTestWithUserHeadPass", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("PostTestWithUserHeadFail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/withOctetStream")
    public ResponseEntity<String> OctetStreamTest(@RequestHeader("MD5") String md5, HttpServletRequest request) throws IOException {
        FileCopyUtils.copy(request.getInputStream(), Files.newOutputStream(Paths.get("logo_color.png")));
        String calMD5 = utils.getMD5(new File("logo_color.png"));
        log.info("md5 = " + md5);
        log.info("calMD5 = " + calMD5);
        if (calMD5.equals(md5)) {
            return ResponseEntity.ok("PostTestWithOctetStreamSuccess");
        } else {
            return ResponseEntity.badRequest().body("PostTestWithOctetStreamFail");
        }
    }

    @PostMapping(path = "/withxwwwformurlencoded")
    public ResponseEntity<String> xwwwformurlencodedTest(@RequestParam("content") String content, @RequestParam("author") String author, @RequestParam("email") String email, @RequestParam("userName") String userName, @RequestParam("passwd") String passwd) {
        if (content.equals("x-www-form-urlencoded Test") && author.equals("LuatTest") && email.equals("yanjunjie@airm2m.com") && userName.equals("yanjunjie") && passwd.equals("1234567890!@#$%^&*()")) {
            return ResponseEntity.ok("postTestWithXwwwformurlencodedSuccess");
        } else {
            return new ResponseEntity<>("postTestWithXwwwformurlencodedFail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/postGPSLocInfo")
    public ResponseEntity<String> postGpsInfo(@RequestBody LbsLocInfo lbsLocInfo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lat", lbsLocInfo.getLat());
        jsonObject.put("lng", lbsLocInfo.getLng());
        jsonObject.put("timestamp", lbsLocInfo.getTimestamp());
        redisTemplate.opsForValue().set("GPSLocInfo", jsonObject.toJSONString());
        return ResponseEntity.ok("postGPSLocInfo OK!");
    }

    @PostMapping(path = "/postCellLocInfo")
    public ResponseEntity<String> postCellLocInfo(@RequestBody LbsLocInfo lbsLocInfo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lat", lbsLocInfo.getLat());
        jsonObject.put("lng", lbsLocInfo.getLng());
        jsonObject.put("timestamp", lbsLocInfo.getTimestamp());
        redisTemplate.opsForValue().set("CellLocInfo", jsonObject.toJSONString());
        return ResponseEntity.ok("postCellLocInfo OK!");
    }


    @PostMapping(path = "/postWiFiLocInfo")
    public ResponseEntity<String> postWiFiLocInfo(@RequestBody LbsLocInfo lbsLocInfo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lat", lbsLocInfo.getLat());
        jsonObject.put("lng", lbsLocInfo.getLng());
        jsonObject.put("timestamp", lbsLocInfo.getTimestamp());
        redisTemplate.opsForValue().set("WiFiLocInfo", jsonObject.toJSONString());
        return ResponseEntity.ok("postWiFiLocInfo OK!");
    }

}
