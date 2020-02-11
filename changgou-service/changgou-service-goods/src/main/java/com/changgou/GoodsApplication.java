package com.changgou;

import entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 描述
 *
 * @author ljh
 * @version 1.0
 * @package com.changgou *
 * @Date 2019-12-27
 * @since 1.0
 */
@SpringBootApplication
@EnableEurekaClient
//组件扫描 将包下的所有的接口 产生代理类交给spring管理
@MapperScan(basePackages = "com.changgou.goods.dao")
public class GoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class,args);
    }
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }
}
