package com.openluat.controller;

import com.alibaba.fastjson.JSONObject;
import com.openluat.pojo.GpsInfo;
import com.openluat.pojo.LbsLocInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
public class PostController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping(path = "/")
    public ResponseEntity<String> postTest(@RequestBody String data) {
        log.info("postTest,data=" + data);
        return ResponseEntity.ok("postTestOK,data:" + data);
    }

    @PostMapping(path = "/getContentLength")
    public ResponseEntity<Integer> getContentLength(@RequestBody String data) throws IOException {
        return ResponseEntity.ok(data.length());
    }

    @PostMapping(path = "/withUserHead")
    public ResponseEntity<String> userHeadTest(@RequestHeader("UserHead") String UserHead) {
        if (UserHead.equals("Jeremy")) {
            return new ResponseEntity<>("postTestWithUserHead Test OK!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("postTestWithUserHead Test Fail!", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/withOctetStream")
    public ResponseEntity<String> OctetStreamTest(HttpServletRequest request) throws IOException {
        FileCopyUtils.copy(request.getInputStream(), Files.newOutputStream(Paths.get("test.lua")));
        return ResponseEntity.ok("postTestWithOctetStream OK!");
    }

    @PostMapping(path = "/withxwwwformurlencoded")
    public ResponseEntity<String> xwwwformurlencodedTest(@RequestParam("content") String content, @RequestParam("author") String author) {
        log.info("postTestWithxwwwformurlencoded.content:" + content);
        log.info("postTestWithxwwwformurlencoded.author:" + author);
        return ResponseEntity.ok("postTestWithxwwwformurlencoded OK!");
    }

    @PostMapping(path = "/postGPSLocInfo")
    public ResponseEntity<String> postGpsInfo(@RequestBody GpsInfo gpsInfo) throws IOException {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formateDate = df.format(date);
        System.out.println(formateDate);
        System.out.println("gpsInfo = " + gpsInfo);
        File file = new File("./GpsInfo.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
        bufferedWriter.write(formateDate);
        bufferedWriter.write("\r\n");
        bufferedWriter.write(gpsInfo.toString());
        bufferedWriter.write("\r\n");
        bufferedWriter.flush();
        bufferedWriter.close();
        return ResponseEntity.ok("PostGpsInfo OK!");
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
