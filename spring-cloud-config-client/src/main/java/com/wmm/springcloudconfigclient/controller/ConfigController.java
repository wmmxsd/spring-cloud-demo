package com.wmm.springcloudconfigclient.controller;

import org.springframework.beans.factory.annotation.Value;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangmingming
 * @Description 配置控制层
 * @date @2020/6/30 11:46
 */
@RestController
public class ConfigController {
    @Value("${config.source}")
    private String source;

    @GetMapping("/source")
    public String source() {
        return source;
    }
}
