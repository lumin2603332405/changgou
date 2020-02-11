package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.service.impl
 * @version 1.0
 * @date 2020/1/9
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Override
    public Map<String, String> login(String username, String password, String grantType, String clientId, String secret) {
        ServiceInstance choose = loadBalancerClient.choose("user-auth");

        String url= "http://"+choose.getHost()+":"+choose.getPort()+"/oauth/token";
        //模拟POST MAN 发送请求 申请令牌

        //请求体对象（包括头信息和请求体信息）
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type",grantType);
        body.add("username",username);
        body.add("password",password);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization","Basic "+Base64.getEncoder().encodeToString(new String(clientId+":"+secret).getBytes()));

        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<MultiValueMap<String,String>>(body,headers);

        ResponseEntity<Map> entity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        Map<String,String> body1 = entity.getBody();

        return body1;
    }

    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode("Y2hhbmdnb3U6Y2hhbmdnb3U=");
        String s = new String(decode);
        System.out.println(s);

    }
}
