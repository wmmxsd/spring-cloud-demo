package com.wmm.springclouddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SpringCloudDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudDemoApplication.class, args);
	}

}
