package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.user.feign
 * @version 1.0
 * @date 2020/1/11
 */
@FeignClient(name="user")
public interface UserFeign {
    @GetMapping("/user/load/{id}")
    public Result<User> loadById(@PathVariable(name="id") String id);


    /**
     * 给指定的用户名 添加积分
     * @param username  用户名
     * @param points  积分数
     * @return
     */
    @GetMapping("/user/points/add")
    public Result addPoints(@RequestParam(name="username") String username,
                            @RequestParam(name="points") Integer points);
}
