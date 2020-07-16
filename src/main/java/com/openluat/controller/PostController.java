package com.openluat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URL;
import java.util.Arrays;

@Slf4j
@RestController
public class PostController {

    @PostMapping(path = "/getContentLength")
    public int postTest(@RequestBody String data) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        /*
         getResource()方法会去classpath下找这个文件，获取到url resource, 得到这个资源后，调用url.getFile获取到 文件 的绝对路径
         */
        URL url = classLoader.getResource("postTestCount.txt");
        /*
          url.getFile() 得到这个文件的绝对路径
         */
        if (url != null) {
            System.out.println(url.getFile());
            File file = new File(url.getFile());
            System.out.println(file.exists());
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            long count = Long.parseLong(bufferedReader.readLine());
            log.info("getContentLength.Count=" + count);

            bufferedReader.close();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(String.valueOf(count + 1));
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        else {
            log.info("getContentLength.postTestCount.txt.FileNotFound");
        }
        return data.length();
    }

}
