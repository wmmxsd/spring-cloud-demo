# Spring Cloud Zuul
> 通俗来讲就是微服务网关。具有以下几个功能：
> - 动态路由：将请求动态的转发到对应的后端微服务
> - 权限校验：对HTTP请求进行权限校验，比如token
> - 监控和统计： 收集相关信息
> - 服务级别的熔断： 当某个服务出现异常时，直接返回我们预设的信息

**[Spring Cloud Zuul官方介绍](https://github.com/Netflix/zuul/wiki)**

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
		<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
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
    name: spring-cloud-zuul
server:
  port: 9060
zuul:
  routes:
    producer:
      path: /producer/**
      serviceId: spring-cloud-producer
      #访问http://localhost:9060/producer/hello?name=2 返回hello 2，this is first message
    consumer:
      path: /consumer/**
      serviceId: spring-cloud-consumer
      #访问http://localhost:9060/consumer/helloWorld/2 返回hello 2，this is first message
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
### 4. 自定义zuul过滤器(Filter是Zuul的核心，用来实现对外服务的控制。Filter的生命周期有4个，分别是“PRE”、“ROUTING”、“POST”、“ERROR”)

```java
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

public class MyFilter extends ZuulFilter {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("{MyFilter}-{serverName}:" + request.getServerName() + ";{method}:" + request.getMethod() + ";{uri}:" + request.getRequestURI());
        // 获取请求的参数
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            //不对其进行路由
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(400);
            ctx.setResponseBody("token is empty");
            ctx.set("isSuccess", false);
            return null;
        } else {
            //对请求进行路由
            ctx.setSendZuulResponse(true);
            ctx.setResponseStatusCode(200);
            ctx.set("isSuccess", true);
            return null;
        }
    }
}
```
### 5. 服务级别的熔断
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Zuul 目前只支持服务级别的熔断，不支持具体到某个URL进行熔断
 * @Description spring-cloud-producer微服务自定义熔断返回类
 * @date @2020/7/1 20:39
 */
@Component
public class SpringCloudProducerFallback implements FallbackProvider {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getRoute() {
        return "spring-cloud-producer";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        log.info("{route}:{}", route);
        if (cause != null && cause.getCause() != null) {
            String reason = cause.getCause().getMessage();
            log.info("Exception {}", reason);
        }
        return fallbackResponse();
    }

    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 500;
            }

            @Override
            public String getStatusText() throws IOException {
                return "Internal Server Error";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("The service is unavailable.".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
```
###6 启动类添加`@EnableZuulProxy`注解，注册MyFilter bean
```java
import com.wmm.springcloudzuul.zuul.MyFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableZuulProxy
public class SpringCloudZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudZuulApplication.class, args);
	}

	@Bean
	public MyFilter myFilter() {
		return new MyFilter();
	}
}
```
### 7.测试
1. ide运行该项目。
2. 启动2个spring-cloud-producer和1个spring-cloud-consumer
3. 访问[http://localhost:9060/producer/hello?name=2](http://localhost:9060/producer/hello?name=2)，页面会返回"token is empty“
4. 访问[http://localhost:9060/producer/hello?name=2&token=1](http://localhost:9060/producer/hello?name=2&token=1)，页面会返回"返回hello 2，this is first message"或者“返回hello 2，this is second message”
6. 关闭其中一个spring-cloud-producer
7. 不断执行步骤3，会发现页面会返回一条正常消息或者“The service is unavailable.”