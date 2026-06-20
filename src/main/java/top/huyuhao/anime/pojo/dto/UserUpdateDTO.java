package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.huyuhao.anime.pojo.User;

/**
 * 用户更新个人信息请求 DTO，只暴露可修改字段，避免直接使用 User 实体。
 */
@Data
@Schema(description = "用户更新个人信息请求体")
public class UserUpdateDTO {

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phoneNumber;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    /**
     * 将 DTO 转换为 User 实体对象（不含 id，id 由调用方设置）
     */
    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPhoneNumber(this.phoneNumber);
        user.setAvatarUrl(this.avatarUrl);
        return user;
    }
}
