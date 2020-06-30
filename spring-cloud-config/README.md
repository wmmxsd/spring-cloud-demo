# Spring Cloud Config Server
> 配置中心服务端

[Spring Cloud Config Server官方介绍](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_server)

- 集中管理各环境的配置文件
- 可以使用git、svn、jdbc、redis及本地磁盘来进行版本管理（默认是git，本demo也是使用的git）
- 支持大的并发查询（HTTP API）
- 支持各种语言

## 使用步骤
### 1. pom中添加依赖

```xml
<properties>
    <java.version>1.8</java.version>
    <spring-cloud.version>Hoxton.SR6</spring-cloud.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    <!--Eureka Client可去掉-->
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
### 2. 启动类中添加`@EnableConfigServer`注解
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * {@link EnableDiscoveryClient}向Eureka Server注册（可去掉）
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
```
### 3. 配置文件添加配置
`application.yml`
```yaml
spring:
  application:
    name: spring-cloud-config-server
  #Spring Cloud Config Server端配置
  cloud:
    config:
      server:
        git:
          uri: https://github.com/wmmxsd/spring-cloud-demo（github仓库路径）
          search-paths: cloud-config（GitHub仓库路径下的文件夹）
          username: GitHub 用户名
          password: GitHub 用户名密码

#该应用端口号
server:
  port: 9051

#注册中心客户端配置（可去掉）
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8001/eureka/

```
### 4. github上传配置文件夹&启动项目
1. 在GitHub某一个仓库新建一个文件夹
2. 文件夹下新建三个文件，分别为[application-dev.yml](https://github.com/wmmxsd/spring-cloud-demo/blob/master/cloud-config/application-dev.yml)、[application-test.yml](https://github.com/wmmxsd/spring-cloud-demo/blob/master/cloud-config/application-test.yml)及[application-pro.yml](https://github.com/wmmxsd/spring-cloud-demo/blob/master/cloud-config/application-pro.yml)
3. ide启动项目

### 5. 测试
> Spring Cloud Config提供了HTTP API来访问配置仓库

#### 访问规则
- /{application}/{profile}[/{label}]
- /{application}-{profile}.yml
- /{label}/{application}-{profile}.yml
- /{application}-{profile}.properties
- /{label}/{application}-{profile}.properties
> 比如说需要访问`https://github.com/wmmxsd/spring-cloud-demo`的master分支下的某一个目录下的`jdbc-dev.yml`。那么`application`就为`jdbc`，`profile`为`dev`，`label`为`master`。

+ [http://localhost:9051/application/dev/master](http://localhost:9051/application/dev/master)
+ [http://localhost:9051/application-dev.yml](http://localhost:9051/application-dev.yml)
+ [http://localhost:9051/master/application-dev.yml](http://localhost:9051/master/application-dev.yml)

