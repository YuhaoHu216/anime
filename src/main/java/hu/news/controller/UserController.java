package hu.news.controller;

import hu.news.pojo.Result;
import hu.news.pojo.User;
import hu.news.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    //先依赖注入
    @Autowired
    private UserService userService;

    //注册功能
    @CrossOrigin
    @PostMapping("/anime/register")
    public Result register(String username,String password){
        log.info("开始注册,用户名{},密码{}",username,password );
        userService.register(username,password);
        return Result.success();

    }

    //登录功能
    @CrossOrigin
    @PostMapping("/anime/login")
    public Result login(String username,String password){
        log.info("开始登录,用户{},密码{}",username,password);
        User u = userService.login(username,password);
        if(u == null){
            return Result.error("还未注册,请先注册");
        }
        return Result.success(u);
    }
}
