# Spring Cloud Consumer

## 服务消费方
- 调用远程服务的服务消费方
- 增加了hystrix熔断器
- 增加了hystrix dashboard熔断器监控工具
## 使用步骤

### 1. pom中添加依赖

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
    <spring-cloud.version>Hoxton.SR6</spring-cloud.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.junit.vintage</groupId>
                <artifactId>junit-vintage-engine</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <!--hystrix熔断器部分 begin-->
    <dependency>
    	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
    	<groupId>org.springframework.cloud</groupId>
    	<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
	<dependency>
    	<groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    </dependency>
	<!--hystrix熔断器部分 end-->
</dependencies>
```
### 2. 启动类中添加`@EnableEurekaClient`注解
```java
/**
 * {@link EnableFeignClients} 开启Feign远程调用其他微服务客户端
 * {@link EnableHystrixDashboard} 开启熔断器仪表面板
 * {@link EnableCircuitBreaker} 开启Hystrix
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrixDashboard
@EnableCircuitBreaker
public class SpringCloudConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConsumerApplication.class, args);
	}

}
```
### 3. HelloWorldController类编写(用于服务消费方远程调用服务提供方)
```java
@RestController
public class ConsumerController {
    private HelloWorldRemote helloWorldRemote;

    @Autowired
    public void setRemote(HelloWorldRemote helloWorldRemote) {
        this.helloWorldRemote = helloWorldRemote;
    }

    @GetMapping("helloWorld/{name}")
    public String consumer(@PathVariable String name) {
        return helloWorldRemote.hello(name);
    }
}
```
### 4. 远程调用接口（使用了Feign）
```java
import com.wmm.springcloudconsumer.remote.hystrix.HelloWorldRemoteHystrix;
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
@FeignClient(name = "spring-cloud-producer", fallback = HelloWorldRemoteHystrix.class)
public interface HelloWorldRemote {
    /**
     *  path必须和远程服务提供方中的url、请求方法种类、参数一致
     * @param name 名称
     * @return 字符串
     */
    @GetMapping("/hello")
    String hello(@RequestParam(value = "name") String name);
}
```
### 5.作用在指定HTTP请求的熔断器
```java
import com.wmm.springcloudconsumer.remote.HelloWorldRemote;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * {@link com.wmm.springcloudconsumer.remote.HelloWorldRemote}的熔断器
 * <p>熔断器特性</p>
 * <p>1、断路器机制：当Hystrix Command请求后端服务失败数量超过一定比例(默认50%), 断路器会切换到开路状态(Open). 这时所有请求会直接失败而不会发送到后端服务.
 * 断路器保持在开路状态一段时间后(默认5秒), 自动切换到半开路状态(HALF-OPEN).
 * 这时会判断下一次请求的返回情况, 如果请求成功, 断路器切回闭路状态(CLOSED), 否则重新切换到开路状态(OPEN)</p>
 * <p>2、Fallback：当请求后端服务出现异常的时候, 可以使用fallback方法返回的值</p>
 * <p>3、资源隔离：在Hystrix中, 主要通过线程池来实现资源隔离. 通常在使用的时候我们会根据调用的远程服务划分出多个线程池
 * 维护多个线程池会对系统带来额外的性能开销. 如果是对性能有严格要求而且确信自己调用服务的客户端代码不会出问题的话, 可以使用Hystrix的信号模式(Semaphores)来隔离资源</p>
 * @author wangmingming
 * @Description HelloWorldRemote的熔断器
 * @date @2020/6/29 10:47
 */
@Component
public class HelloWorldRemoteHystrix implements HelloWorldRemote {
    @Override
    public String hello(@RequestParam(value = "name") String name) {
        return name +" send failed";
    }
}
```
### 6. 配置文件添加配置
_application-peer.yml_
```yaml
spring:
  application:
    name: spring-cloud-consumer
server:
  port: 9002
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8001/eureka/,http://localhost:8002/eureka/,http://localhost:8003/eureka/
    healthcheck:
      enabled: true                           # 开启健康检查（依赖spring-boot-starter-actuator）
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${server.port}
    prefer-ip-address: true #以IP地址注册到服务中心
    lease-renewal-interval-in-seconds: 5      # 心跳时间，即服务续约间隔时间（缺省为30s）
    lease-expiration-duration-in-seconds: 15  # 发呆时间，即服务续约到期时间（缺省为90s）
feign:
  hystrix:
    #开启hystrix熔断器
    enabled: true
#暴露所有健康监控（actuator）接口
management:
  endpoints:
    web:
      exposure:
        include: '*'
```
### 7. 运行
#### 服务消费者远程调用方面
1. ide启动该工程，接下来启动两个`spring-cloud-producer`
2. cd到`spring-cloud-producer`工程的`pom.xml`所在目录
3. 执行`mvn clean package`打包
4. cd到jar包目录（一般在target目录下）
5. 执行`java -jar spring-cloud-producer-0.0.1-SNAPSHOT.jar`，启动一个服务生产者实例
6. 修改`HelloWorldController`的`index`方法的返回值，以此来区分服务消费者远程调用了那个实例
7. 修改`application.yml`中的`port`为`9003`
8. 然后依次执行步骤2、3、4、5，启动第二个服务生产者实例
### 6. 测试
浏览器输入：<http://localhost:9002/helloWorld/consumer>, 然后不断刷新，会反复出现`hello consumer，this is first message`或`hello consumer，this is second message`