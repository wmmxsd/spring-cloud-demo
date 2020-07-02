# Spring Cloud Netflix Turbine

> 把多个hystrix.stream的内容聚合为一个数据源供Dashboard展示。具体点就是将成千上万个http://ip:port/hystrix.stream中返回的json聚合到一个单独的流中，然后在Hystrix Dashboard中显示。

[Spring Cloud Netflix Turbine官方介绍](https://github.com/Netflix/Turbine/wiki)

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
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-turbine</artifactId>
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
### 2. 启动类添加`@EnableHystrixDashboard`及`@EnableTurbine`注解
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
@EnableHystrixDashboard
@EnableTurbine
public class HystrixDashboardTurbineApplication {

	public static void main(String[] args) {
		SpringApplication.run(HystrixDashboardTurbineApplication.class, args);
	}

}
```
### 3. 配置文件添加配置
#### `application.yml`
```yaml
spring:
  application:
    name: hystrix-dashboard-turbine

server:
  port: 9050

turbine:
  #配置Eureka中的serviceId列表，表明监控哪些服务
  app-config: node1,node2
  aggregator:
    #指定聚合哪些集群，多个使用”,”分割，默认为default。可使用http://.../turbine.stream?cluster={clusterConfig之一}访问
    cluster-config: default
  #1.clusterNameExpression指定集群名称，默认表达式appName；此时：turbine.aggregator.clusterConfig需要配置想要监控的应用名称；
  #2.当clusterNameExpression: default时，turbine.aggregator.clusterConfig可以不写，因为默认就是default；
  #3.当clusterNameExpression: metadata[‘cluster’]时，假设想要监控的应用配置了eureka.instance.metadata-map.cluster: ABC，则需要配置，同时turbine.aggregator.clusterConfig: ABC
  cluster-name-expression: new String("default")

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
### 4. 测试
1. cd到`spring-cloud-consumer`工程的`pom.xml`的同级目录下
2. 执行`mvn clean package`
3. `cd target`，进入`target`目录，分别执行`java -jar spring-cloud-consumer-0.0.1-SNAPSHOT.jar --spring.profiles.active=node1`和`java -jar spring-cloud-consumer-0.0.1-SNAPSHOT.jar --spring.profiles.active=node2`
4. 修改`spring-cloud-consumer`工程中`HelloWorldRemote`的`hello`方法名为`hello2`,`ConsumerController`和`HelloWorldRemoteHystrix`类同步修改
5. ide运行`spring-cloud-consumer`，此时运行时使用的默认的配置文件`application.yml`
4. ide运行该turbine工程
5. 访问[http://localhost:9050/turbine.stream](http://localhost:9050/turbine.stream)，页面会返回<br>`: ping`<br>
`data: {"reportingHostsLast10Seconds":0,"name":"meta","type":"meta","timestamp":1593680419639}`
6. 访问[http://localhost:9050/hystrix](http://localhost:9050/hystrix)进入Hystrix Dashboard页面
7. 输入`http://localhost:9050/turbine.stream`后点击`Monitor Stream`进入流监控页面
8. 分别访问并且快速刷新[http://localhost:9002/helloWorld/123](http://localhost:9002/helloWorld/123)、[http://localhost:9005/helloWorld/123](http://localhost:9005/helloWorld/123)、[http://localhost:9006/helloWorld/123](http://localhost:9006/helloWorld/123)，监控页面显示如下：
![not found](https://github.com/wmmxsd/spring-cloud-demo/blob/master/images/turbine.png)