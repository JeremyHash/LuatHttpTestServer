package com.openluat.controller;

import com.openluat.service.QueryAttendanceByPhoneNumber;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class GetController {

    @Autowired
    QueryAttendanceByPhoneNumber queryAttendanceByPhoneNumber;


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

    @GetMapping(path = "/{phoneNumber}")
    public ResponseEntity<String> queryAttendance(@PathVariable String phoneNumber) throws ApiException {
        String pattern = "(13|14|15|18|17)[0-9]{9}";
        boolean matchRes = phoneNumber.matches(pattern);
        if (matchRes) {
            String queryInfo = queryAttendanceByPhoneNumber.query(phoneNumber);

            return new ResponseEntity<>(queryInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("<h1>输入的手机号码<" + phoneNumber + ">有误！</h1>", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/phoneNumber")
    public ResponseEntity<String> queryAttendanceHelp() throws ApiException {

        return new ResponseEntity<>("<h1>请将地址栏中phoneNumber替换为你要查询考勤的手机号</h1>", HttpStatus.OK);
    }

}
