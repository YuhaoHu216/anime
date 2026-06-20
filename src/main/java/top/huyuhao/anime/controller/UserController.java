package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.dto.UserRegisterDTO;
import top.huyuhao.anime.pojo.dto.UserUpdateDTO;
import top.huyuhao.anime.service.UserService;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户注册、登录、个人信息管理")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result register(@RequestBody UserRegisterDTO dto) {
        log.info("注册: account={}", dto.getAccount());
        return userService.register(dto);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "登录成功返回 JWT token，后续请求请在 Authorization 头中携带（Bearer <token>）")
    public Result login(@Parameter(description = "账号") @RequestParam String account,
                        @Parameter(description = "密码") @RequestParam String password) {
        log.info("登录: account={}", account);
        return userService.login(account, password);

    }

    @GetMapping("/me")
    @Operation(summary = "获取当前登录用户信息", description = "从 JWT token 中解析用户身份，无需显式传递 userId")
    public Result getCurrentUser() {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        return userService.getCurrentUser(userId);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息", description = "从 JWT 获取用户身份，无需显式传递 id")
    public Result updateProfile(@RequestBody UserUpdateDTO dto) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        return userService.updateProfile(userId, dto);
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "从 JWT 获取用户身份，无需显式传递 userId")
    public Result updatePassword(@Parameter(description = "旧密码") @RequestParam String oldPassword,
                                  @Parameter(description = "新密码") @RequestParam String newPassword) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        return userService.updatePassword(userId, oldPassword, newPassword);
    }
}
