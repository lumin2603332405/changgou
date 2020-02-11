package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2020/1/8
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class,args);
    }
    //定义一个keyresolver 用于标识网关如何识别用户 以ip?以用户名？
    @Bean(name="ipKeyResolver")// <bean class="" id="">
    public KeyResolver userKeyResolver(){

        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //要以IP地址为识别标识
                String hostString = exchange.getRequest().getRemoteAddress().getHostString();//获取ip地址
                return Mono.just(hostString);
            }
        };
    }
}
