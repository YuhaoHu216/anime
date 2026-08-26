package top.huyuhao.anime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Integer id;
    private Integer animeId;
    private Integer userId;
    /** 子评论的 parentId 指向所属顶层评论（仅一层子评论），顶层评论为 null */
    private Integer parentId;
    /** 回复对象用户ID（子评论 @昵称 用） */
    private Integer replyToUserId;
    private String content;
    /** 图片相对路径 JSON 数组字符串，如 ["comments/1_ab12.png"] */
    private String images;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime createdAt;
    // 非数据库字段：联查用户信息与组装字段
    private String username;
    private String avatarUrl;
    private String replyToUsername;
    private List<String> imageList;
    private Integer replyCount;
    /** 当前用户点赞状态：0=未操作 1=赞 -1=踩 */
    private Integer myType;
    private List<Comment> children;
}
