package com.openluat.controller;


import com.openluat.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@Slf4j
public class BreakDownloadController {

    @Autowired
    private Utils utils;

    @GetMapping("/download/{name}")
    public void breakDownload(@PathVariable String name, HttpServletRequest request, HttpServletResponse response) {
        String fullPath = "static/" + name;
        File downloadFile = new File(fullPath);

        String md5 = utils.getMD5(downloadFile);

        if (!downloadFile.exists()) {
            return;
        }

        ServletContext context = request.getServletContext();
        String mimeType = context.getMimeType(fullPath);
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }

        // set content attributes for the response
        response.setContentType(mimeType);
        // response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
        headerKey = "MD5";
        headerValue = md5;
        response.setHeader(headerKey, headerValue);

        // 解析断点续传相关信息
        response.setHeader("Accept-Ranges", "bytes");
        long downloadSize = downloadFile.length();
        long fromPos = 0, toPos = 0;
        if (request.getHeader("Range") == null) {
            response.setHeader("Content-Length", downloadSize + "");
        } else {
            // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            String range = request.getHeader("Range");
            String bytes = range.replaceAll("bytes=", "");
            String[] ary = bytes.split("-");
            fromPos = Long.parseLong(ary[0]);
            if (ary.length == 2) {
                toPos = Long.parseLong(ary[1]);
            }
            int size;
            if (toPos > fromPos) {
                size = (int) (toPos - fromPos) + 1;
            } else {
                size = (int) (downloadSize - fromPos);
            }
            response.setHeader("Content-Length", size + "");
            downloadSize = size;
        }
        // Copy the stream to the response's output stream.
        RandomAccessFile in = null;
        OutputStream out = null;
        try {
            in = new RandomAccessFile(downloadFile, "rw");
            // 设置下载起始位置
            if (fromPos > 0) {
                in.seek(fromPos);
            }
            // 缓冲区大小
            int bufLen = (int) (downloadSize < 2048 ? downloadSize : 2048);
            byte[] buffer = new byte[bufLen];
            int num;
            int count = 0; // 当前写到客户端的大小
            out = response.getOutputStream();
            while ((num = in.read(buffer)) != -1) {
                out.write(buffer, 0, num);
                count += num;
                //处理最后一段，计算不满缓冲区的大小
                if (downloadSize - count < bufLen) {
                    bufLen = (int) (downloadSize - count);
                    if (bufLen == 0) {
                        break;
                    }
                    buffer = new byte[bufLen];
                }
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.debug("数据被暂停或中断。");
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.debug("数据被暂停或中断。");
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.debug("数据被暂停或中断。");
                }
            }
        }
    }

}
