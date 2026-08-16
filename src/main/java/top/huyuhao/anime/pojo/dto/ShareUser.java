package top.huyuhao.anime.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享主页中的用户基本信息（脱敏）。
 * 绝不包含 email / phoneNumber / password 等敏感字段。
 */
@Data
public class ShareUser {
    private String account;
    private String username;
    private String avatarUrl;
    private LocalDateTime createdAt;
}
