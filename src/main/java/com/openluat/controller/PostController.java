package com.openluat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
public class PostController {

    @PostMapping(path = "/getContentLength")
    public int postTest(@RequestBody String data) throws IOException {
        File file = new File("./postTestCount.txt");
        FileReader fileReader = new FileReader(file);
        FileWriter fileWriter = new FileWriter(file);
        for (; ; ) {
            int n = fileReader.read(); // 反复调用read()方法，直到返回-1
            if (n == -1) {
                break;
            }
            System.out.println((char) n);
        }
        fileReader.close(); // 关闭流
        return data.length();
    }

}
