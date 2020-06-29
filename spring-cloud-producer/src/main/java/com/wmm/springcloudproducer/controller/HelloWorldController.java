package com.wmm.springcloudproducer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangmingming
 * @Description HelloWorld控制层
 * @date @2020/6/28 15:03
 */
@RestController
public class HelloWorldController {
    @GetMapping("/hello")
    public String index(@RequestParam String name) {
        return "hello " + name + "，this is first message";
//        return "hello " + name + "，this is second message";
    }
}
