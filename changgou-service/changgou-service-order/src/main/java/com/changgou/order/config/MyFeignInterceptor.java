package com.changgou.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.config
 * @version 1.0
 * @date 2020/1/11
 */
@Component
public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取当前的请求对象的线程对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            //1.获取当前请求的对象
            HttpServletRequest request = requestAttributes.getRequest();
            //2.获取到所有头信息
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                //3.将头信息传递给下一个微服务的头信息中。
                requestTemplate.header(headerName,headerValue);
            }
        }
    }
}
