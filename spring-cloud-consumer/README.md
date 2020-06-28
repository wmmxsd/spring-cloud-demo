# Spring Cloud Consumer

## 服务消费方
- 调用远程服务的服务消费方
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
集群版配置（规模为两台，需要配置2个application.yml）
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
      defaultZone: http://localhost:8001/eureka/
```
_application-peer2.yml_
```yaml
spring:
  application:
    name: spring-cloud-producer
server:
  port: 9003
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8001/eureka/
```
### 5. 运行
1. cd到`pom.xml`所在目录
2. 执行`mvn clean package`打包
3. cd到jar包目录
4. 执行`java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1`，启动一个服务生产者实例
5. 修改`HelloWorldController`的`index`方法的返回值，以此来区分服务消费者远程调用了那个实例
6. 依次执行步骤1、2、3，然后执行`java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer2`，启动第二个服务生产者实例
### 6. 浏览器输入：<http://localhost:8001/>, 效果入下图所示：
![not found](https://github.com/wmmxsd/spring-cloud-demo/blob/master/images/demo1.jpg)