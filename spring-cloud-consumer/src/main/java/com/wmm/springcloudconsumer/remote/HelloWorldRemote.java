package com.wmm.springcloudconsumer.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign是一个声明式Web Service客户端。使用Feign能让编写Web Service客户端更加简单, 它的使用方法是定义一个接口，然后在上面添加注解，同时也支持JAX-RS标准的注解。
 * Feign也支持可拔插式的编码器和解码器。Spring Cloud对Feign进行了封装，使其支持了Spring MVC标准注解和HttpMessageConverters。Feign可以与Eureka和Ribbon组合使用以支持负载均衡
 * name:spring.application.name配置的名称
 * 此接口中的方法和远程服务中controller中的方法的属性必须和远程服务提供方中的url、请求方法种类、参数一致
 * @author wangmingming
 * @Description 远程调用服务名为spring-cloud-producer的url为/hello的方法
 * @date @2020/6/28 16:33
 */
@FeignClient(name = "spring-cloud-producer")
public interface HelloWorldRemote {
    /**
     *  path必须和远程服务提供方中的url、请求方法种类、参数一致
     * @param name 名称
     * @return 字符串
     */
    @GetMapping("/hello")
    String hello(@RequestParam String name);
}
