package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.User;
import top.huyuhao.anime.service.UserService;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户注册、登录、个人信息管理")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result register(@RequestBody User user) {
        log.info("注册: account={}", user.getAccount());
        try {
            userService.register(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result login(@Parameter(description = "账号") @RequestParam String account,
                        @Parameter(description = "密码") @RequestParam String password) {
        log.info("登录: account={}", account);
        try {
            User user = userService.login(account, password);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "获取用户信息")
    public Result profile(@Parameter(description = "用户ID") @RequestParam Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    public Result updateProfile(@RequestBody User user) {
        try {
            userService.updateProfile(user);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result updatePassword(@Parameter(description = "用户ID") @RequestParam Integer id,
                                  @Parameter(description = "旧密码") @RequestParam String oldPassword,
                                  @Parameter(description = "新密码") @RequestParam String newPassword) {
        try {
            userService.updatePassword(id, oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
