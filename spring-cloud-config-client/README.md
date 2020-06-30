# Spring Cloud Config Client
> 配置中心客户端

[Spring Cloud Config Client官方介绍](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_client)

## 功能
- 可以立即使用`Spring Config Server`管理的配置文件

## 使用步骤
### 1. pom中添加依赖

```xml
<properties>
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
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <!--Eureka 客户端（可以去到）-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
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
### 2. 启动类只需要`@SpringBootApplication`注解就可以
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * {@link EnableDiscoveryClient}向Eureka Server注册(可去掉)
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigClientApplication.class, args);
	}

}
```
### 3. 配置文件添加配置
#### `application.yml`
```yaml
spring:
  application:
    name: spring-cloud-config-client

server:
  port: 9052

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8001/eureka/
```
#### `bootstrap.yml`
> 想要消费Spring Cloud Config Server上的配置必须提供一个bootstrap.yml文件，该配置文件中需要配置Spring Cloud Config Server相关信息
> **若项目运行使用的本地`application.yml`和远程的`application.yml`有相同项的配置，则Spring Cloud Config会使用远程的`application.yml`中的那个配置**
```yaml
spring:
  cloud:
    config:
      name: application
      profile: dev
      uri: http://localhost:9051
      label: master
```
### 4. Controller编写，便于web测试
```java
@RestController
public class ConfigController {
    @Value("${config.source}")
    private String source;

    @GetMapping("/source")
    public String source() {
        return source;
    }
}
```
### 5.测试
1. ide运行
2. 访问[http://localhost:3/source](http://localhost:3/source)，页面会返回"github"