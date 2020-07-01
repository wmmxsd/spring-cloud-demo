package com.wmm.springcloudzuul.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * @author wangmingming
 * @Description 自定义zuul过滤器
 * @date @2020/7/1 20:15
 */
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
