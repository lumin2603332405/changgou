package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/***
 * 用于接收请求 展示登录页面
 * @author ljh
 * @packagename com.changgou.oauth.controller
 * @version 1.0
 * @date 2020/1/12
 */
@Controller
@RequestMapping("/oauth")
public class LoginRedirect {

    @RequestMapping("/login")
    public String showLogin(Model model,String from) {
        model.addAttribute("url",from);
        return "login";
    }
}
