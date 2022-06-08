package com.njust.controller;


import com.njust.MinIOConfig;
import com.njust.grace.result.GraceJSONResult;
import com.njust.utils.MinIOUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("file")
@Slf4j
@Api(tags="文件上传接口")
public class FileController {

    @Autowired
    private MinIOConfig minIOConfig;    //其实就是把配置文件对象化

    @PostMapping("upload")  //文件上传要用post
    public GraceJSONResult upload(MultipartFile file) throws Exception {
        String fileName=file.getOriginalFilename();

        MinIOUtils.uploadFile(minIOConfig.getBucketName(),fileName,file.getInputStream());

        String imgUrl= minIOConfig.getFileHost()+"/"+minIOConfig.getBucketName()+"/"+fileName;
        return GraceJSONResult.ok(imgUrl);
    }
}
