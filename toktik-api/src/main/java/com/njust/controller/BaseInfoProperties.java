package com.njust.controller;

import com.njust.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;


//提供一些公用的方法,让别的Controller来继承
public class BaseInfoProperties {
    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
}
