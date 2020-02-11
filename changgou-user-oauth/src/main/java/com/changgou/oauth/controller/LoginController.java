package com.changgou.oauth.controller;

import com.changgou.oauth.service.LoginService;
import com.changgou.oauth.util.CookieUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.controller
 * @version 1.0
 * @date 2020/1/9
 */
@RestController
@RequestMapping("/user")
public class LoginController {
    private static final String GRANT_TYPE="password";
    private static final String CLIENT_ID="changgou";
    private static final String SECRET="changgou";

    @Autowired
    private LoginService loginService;

    //Cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    /**
     * 根据用户名和密码 来进行登录（模拟client 向认证服务器申请令牌，返回令牌信息）
     * @return
     */
    @RequestMapping("/login")
    public Result login(String username,String password){
        //1.用户名
        //2.密码
        //3.写死授权模式 这里采用密码模式 password
        //4.客户端id    写死
        //5.客户端密码  写死
        //6.模拟浏览器（POSTMAN）申请令牌

       Map<String,String> tokenmap = loginService.login(username,password,GRANT_TYPE,CLIENT_ID,SECRET);

        saveCookie(tokenmap.get("access_token"));
        //7.返回
        return new Result(true, StatusCode.OK,"申请令牌成功",tokenmap);
    }


    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}
