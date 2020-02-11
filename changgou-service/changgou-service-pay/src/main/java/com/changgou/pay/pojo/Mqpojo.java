package com.changgou.pay.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.pay.pojo
 * @version 1.0
 * @date 2020/1/15
 */
@ConfigurationProperties(prefix = "abc")
@Component
public class Mqpojo {
    private String username;
    private String age;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
