package com.njust.controller;


import com.njust.grace.result.GraceJSONResult;
import com.njust.model.Stu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.njust.grace.result.ResponseStatusEnum.*;

@RestController
@RequestMapping("/hello")
@Slf4j  //日志
@Api(tags="Hello 测试的接口")    //显示在Knife4j的文档中,HelloController测试的名字
public class HelloController {

    @ApiOperation(value="hello - 这是一个hello的测试路由")
    @GetMapping("")
    public Object hello(){
        Stu stu=new Stu("yfx",24);
        log.info(stu.toString());
        log.debug(stu.toString());
        log.error(stu.toString());
        log.warn(stu.toString());
//        return GraceJSONResult.errorCustom(SYSTEM_ERROR_GLOBAL);
        return GraceJSONResult.ok(stu);
    }
}
