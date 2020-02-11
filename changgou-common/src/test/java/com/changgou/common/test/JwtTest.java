package com.changgou.common.test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.common.test
 * @version 1.0
 * @date 2020/1/8
 */
public class JwtTest {

    //创建令牌（颁发令牌）
    @Test
    public void createJwt() {
        JwtBuilder builder = Jwts.builder();
        //1.设置头部 默认的不用设置
        //2.设置载荷
        builder.setId("afafa")//唯一标识
                .setSubject("我是一个主题")//主题
                .setIssuer("传智播客")//颁发者
                .setIssuedAt(new Date())//颁发日期
                .setExpiration(new Date())//设置jwt的有效期
                //3.设置签名
                .signWith(SignatureAlgorithm.HS256, "itcastmy");
        //自定义载荷信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 22);
        builder.addClaims(map);// {name:zhangsan,age:22}
        //4.生成令牌
        String compact = builder.compact();
        System.out.println(compact);
    }

    //解析令牌
    @Test
    public void parseJwt() {
        String compact = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhZmFmYSIsInN1YiI6IuaIkeaYr-S4gOS4quS4u-mimCIsImlzcyI6IuS8oOaZuuaSreWuoiIsImlhdCI6MTU3ODQ3NTg4OCwiZXhwIjoxNTc4NDc1ODg4LCJuYW1lIjoiemhhbmdzYW4iLCJhZ2UiOjIyfQ.lWkwVsmH8LeC7lLLQJSOqxpIXGj0tXWDKAqgQPYtpxQ";
        Claims itcastmy = Jwts.parser()
                .setSigningKey("itcastmy")
                .parseClaimsJws(compact)
                .getBody();

        System.out.println(itcastmy);

    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }

}
