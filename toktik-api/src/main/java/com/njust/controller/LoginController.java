package com.njust.controller;


import com.njust.grace.result.GraceJSONResult;
import com.njust.utils.IPUtil;
import com.njust.utils.SMSUtils;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j  //用于日志log error info等
@Api(tags="登录接口模块") //用于生成knife4j的文档
@RestController
@RequestMapping("passport")
public class LoginController extends BaseInfoProperties{
    @Autowired
    private SMSUtils smsUtils;

    @GetMapping("getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String mobile, HttpServletRequest request) throws Exception {
        if(StringUtils.isBlank(mobile)){
            return GraceJSONResult.errorMsg("输入的手机号为空");
        }
        //获取用户ip
        String userIp= IPUtil.getRequestIp(request);
        redis.setnx60s(MOBILE_SMSCODE+":"+userIp,"1");  //value随便填
        log.info("用户的ip地址是"+userIp);
        //todo根据ip限制60s内只能获取一次验证码

        String code=String.valueOf((int)((Math.random()*9+1)*1000));
        //*9+1目的是防止第一位为0,转为int后会丢失
        //random范围[0,1)  *9后[0,9) +1后 [1,10) 不会多出一位
        log.info("验证码是:"+code);
//        smsUtils.sendSMS(mobile,code);
        //验证码放入redis中,用于后续的验证
        redis.set(MOBILE_SMSCODE+":"+mobile,code,30*60);
        return GraceJSONResult.ok();
    }

}
