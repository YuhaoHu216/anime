package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "评论点赞/点踩请求体")
public class CommentLikeDTO {

    @Schema(description = "评论ID", example = "1")
    private Integer commentId;

    @Schema(description = "类型：1=赞 -1=踩", example = "1")
    private Integer type;
}
