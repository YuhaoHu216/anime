package hu.news.controller;

import hu.news.pojo.Result;
import hu.news.pojo.User;
import hu.news.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

    //先依赖注入
    @Autowired
    private UserService userService;

    //注册功能
    @PostMapping("/anime/register")
    public Result register(@RequestBody User user){
        log.info("开始注册,用户{}", user);
        userService.register(user);
        return Result.success();

    }
}
