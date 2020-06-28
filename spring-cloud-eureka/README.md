# Spring Cloud Eureka
## 提供服务注册中心的功能
- 作为服务注册中心。所有服务都能够在该服务器上注册
- 提供网页来简化与服务器的交互
- 作为轮询负载均衡器。在服务消费者调用服务生产者时采用负载均衡的方式来实现调用
- 故障转移功能。定时查看各个客户端，查询没有反应，则会从服务器的注册列表中删除
## 使用步骤
### 1. pom中添加依赖
```
<properties>
    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <spring-cloud.version>Hoxton.SR5</spring-cloud.version>
</properties>

<dependencies>
    <!--<dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter</artifactId>
    </dependency>-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```
### 2. 启动类中添加`@EnableEurekaServer`注解
```
@SpringBootApplication
@EnableEurekaServer
public class SpringCloudEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudEurekaApplication.class, args);
	}

}
```
### 3. 配置文件添加配置
单机版配置（`application.yml`）
```
spring:
  application:
    name: spring-cloud-eureka

server:
  port: 8000

eureka:
  client:
    #表示是否将自己注册到Eureka Server，默认为true
    register-with-eureka: false
    #表示是否从Eureka Server获取注册信息，默认为true
    fetch-registry: false
    #设置与Eureka Server交互的地址，查询服务和注册服务都需要依赖这个地址。
    #默认是http://localhost:8761/eureka ；多个地址可使用 , 分隔
    serviceUrl:
      defaultZone: http://localhost:${server.port}/eureka/
```
集群版配置（需要配置三个application.yml）

_application-peer1.yml_
```
spring:
  application:
    name: spring-cloud-eureka
  profiles: peer1

server:
  port: 8001

eureka:
  instance:
    appname: spring-cloud-eureka
    hostname: peer1
    prefer-ip-address: false
  client:
    #表示是否将自己注册到Eureka Server
    register-with-eureka: true
    #表示是否从Eureka Server获取注册信息
    fetch-registry: true
    #设置与Eureka Server交互的地址，查询服务和注册服务都需要依赖这个地址。
    #默认是http://localhost:8761/eureka ；多个地址可使用 , 分隔
    serviceUrl:
      #双节点模式配置
      #defaultZone: http://peer2:8002/eureka/
      #集群模式配置
      defaultZone: http://peer2:8002/eureka/,http://peer3:8003/eureka/
```
_application-peer2.yml_
```
spring:
  application:
    name: spring-cloud-eureka
  profiles: peer2

server:
  port: 8002

eureka:
  instance:
    appname: spring-cloud-eureka
    hostname: peer2
    prefer-ip-address: false
  client:
    #表示是否将自己注册到Eureka Server
    register-with-eureka: true
    #表示是否从Eureka Server获取注册信息
    fetch-registry: true
    #设置与Eureka Server交互的地址，查询服务和注册服务都需要依赖这个地址。
    #默认是http://localhost:8761/eureka ；多个地址可使用 , 分隔
    serviceUrl:
      #双节点模式配置
      #defaultZone: http://peer1:8001/eureka/
      #集群模式配置
      defaultZone: http://peer1:8001/eureka/,http://peer3:8003/eureka/
```
_application-peer3.yml_
```
spring:
  application:
    name: spring-cloud-eureka
  profiles: peer3

server:
  port: 8003

eureka:
  instance:
    appname: spring-cloud-eureka
    hostname: peer3
    prefer-ip-address: false
  client:
    #表示是否将自己注册到Eureka Server
    register-with-eureka: true
    #表示是否从Eureka Server获取注册信息
    fetch-registry: true
    #设置与Eureka Server交互的地址，查询服务和注册服务都需要依赖这个地址。
    #默认是http://localhost:8761/eureka ；多个地址可使用 , 分隔
    serviceUrl:
      #集群模式配置
      defaultZone: http://peer1:8001/eureka/,http://peer2:8002/eureka/
```
### 4. host文件修改
打开`C:\Windows\System32\drivers\etc\hosts`<br>
末尾加上
```
127.0.0.1 peer1
127.0.0.1 peer2
127.0.0.1 peer3
```
### 5. 运行
1. cd到`pom.xml`所在目录
2. 执行`mvn clean package`打包
3. cd到jar包目录
4. 单机版启动：执行`java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar`/直接在ide中启动	
5. 集群版启动
```
java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1
java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer2
java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer3
```
### 6. 浏览器输入：<http://localhost:8001/>, General Info中`registered-replicas`和`available-replicas`的值为`http://peer1:8001/eureka/,http://peer2:8002/eureka/,http://peer3:8003/eureka/`