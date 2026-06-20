package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.huyuhao.anime.pojo.User;

/**
 * 用户注册请求 DTO，只暴露注册所需字段，避免直接使用 User 实体。
 */
@Data
@Schema(description = "用户注册请求体")
public class UserRegisterDTO {

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "账号", example = "zhangsan")
    private String account;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "手机号", example = "13800138000")
    private String phoneNumber;

    /**
     * 将 DTO 转换为 User 实体对象
     */
    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setAccount(this.account);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setPhoneNumber(this.phoneNumber);
        return user;
    }
}
