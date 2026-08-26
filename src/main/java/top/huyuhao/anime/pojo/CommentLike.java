package top.huyuhao.anime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLike {
    private Integer id;
    private Integer commentId;
    private Integer userId;
    /** 1=赞 -1=踩 */
    private Integer type;
    private LocalDateTime createdAt;
}
