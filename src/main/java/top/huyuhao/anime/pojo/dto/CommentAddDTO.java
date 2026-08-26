package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 发表评论请求 DTO，userId 由 JWT 获取。
 */
@Data
@Schema(description = "发表评论请求体")
public class CommentAddDTO {

    @Schema(description = "动漫ID", example = "1")
    private Integer animeId;

    @Schema(description = "文字内容（纯图片评论可留空）", example = "太好看了")
    private String content;

    @Schema(description = "已上传的图片相对路径列表，最多9张", example = "[\"comments/1_ab12.png\"]")
    private List<String> images;

    @Schema(description = "顶层评论ID（回复时填；顶层评论为 null）", example = "5")
    private Integer parentId;

    @Schema(description = "回复对象用户ID（子评论 @昵称 用）", example = "2")
    private Integer replyToUserId;
}
