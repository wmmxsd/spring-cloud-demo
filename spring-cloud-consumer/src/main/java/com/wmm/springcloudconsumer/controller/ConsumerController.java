package com.wmm.springcloudconsumer.controller;

import com.wmm.springcloudconsumer.remote.HelloWorldRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangmingming160328
 * @Description 消费的控制层
 * @date @2020/6/28 16:39
 */
@RestController
public class ConsumerController {
    @Autowired
    private HelloWorldRemote remote;

    @GetMapping("helloWorld/{name}")
    public String consumer(@PathVariable String name) {
        return remote.index(name);
    }
}
