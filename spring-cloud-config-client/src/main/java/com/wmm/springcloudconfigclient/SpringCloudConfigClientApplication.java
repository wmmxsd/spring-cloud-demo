package com.wmm.springcloudconfigclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * {@link EnableDiscoveryClient}向Eureka Server注册
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigClientApplication.class, args);
	}

}
