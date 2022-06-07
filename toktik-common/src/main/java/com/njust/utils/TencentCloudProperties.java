package com.njust.utils;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data   //生成getter和setter
@Component  //交给Spring容器管理
@PropertySource("classpath:tencentcloud.properties")
@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentCloudProperties {
    private String secretId;
    private String secretKey;
}
