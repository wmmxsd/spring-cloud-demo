package com.wmm.springcloudconfigclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link RefreshScope}加上该注解后必须先将Spring Cloud Config Server启动后再启动该工程
 * @author wangmingming
 * @Description 配置控制层
 * @date @2020/6/30 11:46
 */
@RestController
@RefreshScope
public class ConfigController {
    @Value("${config.source}")
    private String source;

    @GetMapping("/source")
    public String source() {
        return source;
    }
}
