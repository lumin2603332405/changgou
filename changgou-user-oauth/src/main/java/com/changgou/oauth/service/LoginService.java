package com.changgou.oauth.service;

import java.util.Map;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.oauth.service
 * @version 1.0
 * @date 2020/1/9
 */
public interface LoginService {
    Map<String,String> login(String username, String password, String grantType, String clientId, String secret);

}
