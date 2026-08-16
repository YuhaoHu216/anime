package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户隐私设置 DTO —— 个人信息 / 收藏夹 / 追番记录 各自是否公开。
 * 同时用作读取（响应）与更新（请求）两种场景。
 */
@Data
@Schema(description = "用户隐私设置")
public class PrivacyUpdateDTO {

    @Schema(description = "个人信息是否公开", example = "true")
    private Boolean profilePublic;

    @Schema(description = "收藏夹是否公开", example = "false")
    private Boolean collectionPublic;

    @Schema(description = "追番记录是否公开", example = "false")
    private Boolean watchPublic;
}
