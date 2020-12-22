package com.openluat.controller;


import com.openluat.service.QABPService;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class GetController {

    @Autowired
    private QABPService qabpService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping(path = "/")
    public ResponseEntity<String> getTest(@RequestParam("test1") String test1, @RequestParam("test2") String test2, @RequestParam("test3") String test3, @RequestParam("test4") String test4, @RequestParam("test5") String test5, @RequestParam("test6") String test6) {
        if (test1.equals("1") && test2.equals("22") && test3.equals("333") && test4.equals("四四四四") && test5.equals("FiveFiveFiveFiveFive") && test6.equals("ろくろくろくろくろくろく")) {
            return new ResponseEntity<>("getTestSuccess", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("getTestFail", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/waitTest")
    public ResponseEntity<String> waitTest() throws InterruptedException {
        Thread.sleep(15000);
        return new ResponseEntity<>("waitTestSuccess", HttpStatus.OK);
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

    @GetMapping(path = "/{phoneNumber}")
    public ResponseEntity<String> queryAttendance(@PathVariable String phoneNumber) throws ApiException {
        String pattern = "(13|14|15|16|17|18)[0-9]{9}";
        boolean matchRes = phoneNumber.matches(pattern);
        if (matchRes) {
            String queryInfo = qabpService.query(phoneNumber);
            return new ResponseEntity<>(queryInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("<h1>输入的手机号码<" + phoneNumber + ">有误！</h1>", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/phoneNumber")
    public ResponseEntity<String> queryAttendanceHelp() {

        return new ResponseEntity<>("<h1>请将地址栏中phoneNumber替换为你要查询考勤的手机号</h1>", HttpStatus.OK);
    }

    @GetMapping(path = "/getCellLocInfo")
    public ResponseEntity<String> getCellLocInfo() {
        String cellLocInfo = redisTemplate.opsForValue().get("CellLocInfo");
        if (cellLocInfo == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(cellLocInfo);
        }
    }


    @GetMapping(path = "/getWiFiLocInfo")
    public ResponseEntity<String> getWiFiLocInfo() {
        String wiFiLocInfo = redisTemplate.opsForValue().get("WiFiLocInfo");
        if (wiFiLocInfo == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(wiFiLocInfo);
        }
    }

    @GetMapping(path = "/getGPSLocInfo")
    public ResponseEntity<String> getGPSLocInfo() {
        String gpsLocInfo = redisTemplate.opsForValue().get("GPSLocInfo");
        if (gpsLocInfo == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(gpsLocInfo);
        }
    }

}
