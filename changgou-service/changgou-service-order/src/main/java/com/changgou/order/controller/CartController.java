package com.changgou.order.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.plugin.liveconnect.SecurityContextHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.order.controller
 * @version 1.0
 * @date 2020/1/11
 */
@RestController
@RequestMapping("/cart")
public class CartController {


    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;
    /**
     *
     */
    @RequestMapping("/add")
    public Result add(Long id, Integer num) {
        //动态获取用户的信息 ： 解析令牌获取里面的USERNAME的属性值
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        String username = userInfo.get("username");
        cartService.add(username,id,num);
        return new Result(true, StatusCode.OK,"添加购物车成功");
    }

    @RequestMapping("/list")
    public Result<List<OrderItem>> findCartList(){
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        String username = userInfo.get("username");
        List<OrderItem> orderItems = cartService.list(username);
        return new Result(true, StatusCode.OK,"获取购物车成功",orderItems);
    }


}
