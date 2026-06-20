package top.huyuhao.anime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anime {
    private Integer id;
    private String nameCn;
    private String nameJp;
    private String broadcastTime;
    private String bangumiScore;
    private Integer episode;
    private String coverUrl;
    private String description;
    // 审核相关
    private String reviewStatus;
    private Integer submittedBy;
    private Integer reviewedBy;
    private String reviewComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 非数据库字段：关联的标签列表

    private List<Tag> tags;
}
