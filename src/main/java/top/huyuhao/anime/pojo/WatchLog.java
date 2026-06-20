package top.huyuhao.anime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchLog {
    private Integer id;
    private Integer userId;
    private Integer animeId;
    private LocalDate watchDate;
    private Integer epStart;
    private Integer epEnd;
    private Integer epCount;
    private String notes;
    private LocalDateTime createdAt;
    // 非数据库字段：关联的动漫信息（用于列表展示）
    private String animeNameCn;
    private String animeCoverUrl;
}
