package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.context.UserContext;
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
            return userService.register(user);

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
        User user = userService.getCurrentUser(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @GetMapping("/profile")
    @Operation(summary = "获取用户信息（兼容旧接口）", description = "支持显式传 id 或从 JWT 获取")
    public Result profile(@Parameter(description = "用户ID（可选，不传则从 JWT 获取）") @RequestParam(required = false) Integer id) {
        Integer userId = id != null ? id : UserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息", description = "从 JWT 获取用户身份，无需显式传递 id")
    public Result updateProfile(@RequestBody User user) {
        try {
            // 从 JWT 获取当前用户 ID，防止越权修改他人信息
            user.setId(UserContext.getUserId());
            userService.updateProfile(user);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "从 JWT 获取用户身份，无需显式传递 userId")
    public Result updatePassword(@Parameter(description = "旧密码") @RequestParam String oldPassword,
                                  @Parameter(description = "新密码") @RequestParam String newPassword) {
        try {
            Integer userId = UserContext.getUserId();
            if (userId == null) {
                return Result.error("未登录");
            }
            userService.updatePassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
