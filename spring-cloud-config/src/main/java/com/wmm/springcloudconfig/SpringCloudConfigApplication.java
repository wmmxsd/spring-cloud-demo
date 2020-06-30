package com.wmm.springcloudconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * {@link EnableDiscoveryClient}向Eureka Server注册
 * {@link EnableConfigServer}激活对配置中心的支持
 */
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class SpringCloudConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigApplication.class, args);
    }

}
