package com.jia.controller;

import com.jia.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传和下载
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${img.path}")
    private String imgPath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info(file.toString());

        //获得原始文件名
        String fileName = file.getOriginalFilename();
        //获得原始文件名后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //修改文件名
        String UUIDFileName = UUID.randomUUID() +suffix;
        //判断是否有该目录
        File dir = new File(imgPath);
        if (!dir.exists()){
            dir.mkdir();
        }

        file.transferTo(new File(imgPath + UUIDFileName));
        return R.success(UUIDFileName);

    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        FileInputStream iutputStream= new FileInputStream(imgPath +name);

        ServletOutputStream outputStream = response.getOutputStream();

        response.setContentType("image/jpeg");

        int len =0;
        byte[] bytes =new byte[1024];

        while ((len =iutputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }

        iutputStream.close();
        outputStream.close();


    }

}
