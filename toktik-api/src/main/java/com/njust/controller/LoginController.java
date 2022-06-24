package com.njust.controller;


import com.njust.base.BaseInfoProperties;
import com.njust.bo.RegistLoginBO;
import com.njust.grace.result.GraceJSONResult;
import com.njust.grace.result.ResponseStatusEnum;
import com.njust.pojo.Users;
import com.njust.service.UserService;
import com.njust.utils.IPUtil;
import com.njust.utils.SMSUtils;
import com.njust.vo.UsersVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@Slf4j  //用于日志log error info等
@Api(tags = "登录接口") //用于生成knife4j的文档
@RestController
@RequestMapping("passport")
public class LoginController extends BaseInfoProperties {
    @Autowired
    private SMSUtils smsUtils;

    @Autowired
    private UserService userService;

    @PostMapping("getSMSCode")
    public GraceJSONResult getSMSCode(@RequestParam String mobile, HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.errorMsg("输入的手机号为空");
        }
        //获取用户ip
        String userIp = IPUtil.getRequestIp(request);
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, "1");  //value随便填
        log.info("用户的ip地址是" + userIp);
        //todo根据ip限制60s内只能获取一次验证码

        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        //*9+1目的是防止第一位为0,转为int后会丢失
        //random范围[0,1)  *9后[0,9) +1后 [1,10) 不会多出一位
        log.info("验证码是:" + code);
        smsUtils.sendSMS(mobile,code);  //腾讯云验证码
        // 验证码放入redis中,用于后续的验证
        redis.set(MOBILE_SMSCODE + ":" + mobile, code, 30 * 60);
        return GraceJSONResult.ok();
    }

    @PostMapping("login")
    public GraceJSONResult login(@Valid/*开启校验*/ @RequestBody RegistLoginBO registLoginBO,
                                 HttpServletRequest request) {
        String mobile = registLoginBO.getMobile();
        String code = registLoginBO.getSmsCode();
        //检查验证码是否正确
        String redisCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        //检查用户是否已经存在:若不存在则注册
        Users user = userService.queryMobileIsExist(mobile);
        if (user == null) {
            user = userService.createUser(mobile);
        }
        //保存用户信息
        String uToken = UUID.randomUUID().toString();
        redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);    //过期时间:默认永久
        //注意:如果多端登录,那么此设备的登录信息会把之前的挤掉(之前的token失效)

        //用户登录注册成功以后,删除redis中的短信验证码
        redis.del(MOBILE_SMSCODE + ":" + mobile);
        //返回用户信息,包含token令牌
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO); //Spring提供的工具,拷贝内容
        usersVO.setUserToken(uToken);
        log.info("登录成功");
        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("logout")
    public GraceJSONResult logout(@RequestParam String userId){
        //检查userId是否存在
        redis.del(REDIS_USER_TOKEN+":"+userId);
        //todo 多端怎么处理?
        log.info("退出登录成功");
        return GraceJSONResult.ok();
    }
}
