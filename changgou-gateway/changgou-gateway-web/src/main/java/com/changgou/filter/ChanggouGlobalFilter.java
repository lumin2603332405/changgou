package com.changgou.filter;

import com.changgou.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/***
 * 全局过滤器： 目的： 拦截所有的请求 进行令牌的解析 判断。如果解析成功放行，解析失败 直接返回401
 * @author ljh
 * @packagename com.changgou.filter
 * @version 1.0
 * @date 2020/1/8
 */
@Component
public class ChanggouGlobalFilter implements GlobalFilter, Ordered {
    public static final String Authorization_KEY = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取到request对象
        ServerHttpRequest request = exchange.getRequest();
        //2.获取到response对象
        ServerHttpResponse response = exchange.getResponse();

        //3.获取当前的请求路径 判断 如果是登录，放行
        String path = request.getURI().toString();
        if (path.startsWith("/api/user/login")) {
            //放行
            return chain.filter(exchange);
        }
        //4.1.先从头信息获取token ---》如果获取不到 再去 请求参数中获取
        String token = request.getHeaders().getFirst(Authorization_KEY);
        if (StringUtils.isEmpty(token)) {
            //4.2 从请求参数中获取token -->如果获取不到 再去 cookie中获取
            token = request.getQueryParams().getFirst(Authorization_KEY);
        }

        if (StringUtils.isEmpty(token)) {
            //4.3 从cookie中获取token --->如果获取不到 ，直接返回 401
            HttpCookie cookie = request.getCookies().getFirst(Authorization_KEY);
            if(cookie!=null){
                token = cookie.getValue();
            }
        }
        // 直接返回 401  --->重定向到登录的页面
        if (StringUtils.isEmpty(token)) {
            //设置 重定向的状态码

            response.setStatusCode(HttpStatus.SEE_OTHER);
            //设置 重定向到的路径是什么

            //动态获取当前的请求的路径 ：其实就是 http://localhost:8001/api/user
            response.getHeaders().set("Location","http://localhost:9001/oauth/login?from="+path);
            return response.setComplete();//完成请求
        }

        //4.4 如果能获取到token ,需要解析 解析失败 直接返回，解析成功 放行请求

        //网关 获取到的令牌信息 将令牌信息传递给下一个微服务
        request.mutate().header("Authorization","bearer "+token);//添加头部信息

        /*try {
            JwtUtil.parseJWT(token);
            //解析成功 放行
            return chain.filter(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            //解析失败
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//完成请求
        }*/




        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
