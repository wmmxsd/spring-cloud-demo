package com.wmm.springcloudconsumer.remote.hystrix;

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
