# Spring Cloud Producer
## 服务提供方
- 向服务中心注册并定时发送心跳
## 使用步骤
### 1. pom中添加依赖
```xml
<properties>
    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
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
</dependencies>
```
### 2. 启动类中添加`@EnableEurekaClient`注解
```java
@SpringBootApplication
@EnableEurekaClient
public class SpringCloudProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudProducerApplication.class, args);
	}

}
```
### 3. HelloWorldController类编写(用于服务消费方调用)
```java
@RestController
public class HelloWorldController {
    @GetMapping("/hello")
    public String index(@RequestParam String name) {
        //return "hello " + name + "，this is first message";
        return "hello " + name + "，this is second message";
    }
}
```
### 4. 配置文件添加配置
集群版配置(规模为两台，需要配置2个application.yml)

_application-peer1.yml_
```yaml
spring:
  application:
    name: spring-cloud-producer
server:
  port: 9001
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
```
_application-peer2.yml_(暂时用不到，服务消费方调用服务提供方集群时会用到)
```yaml
spring:
  application:
    name: spring-cloud-producer
server:
  port: 9003
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
```
### 5. 运行
1. cd到`pom.xml`所在目录
2. 执行`mvn clean package`打包
3. cd到jar包目录
4. 执行`java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1`，启动一个服务生产者实例
### 6. 测试
浏览器输入：<http://localhost:9001/hello?name=test>, 页面会显示`hello test，this is first message`