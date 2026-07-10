package top.huyuhao.anime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionItem {
    private Integer id;
    private Integer collectionId;
    private Integer animeId;
    private LocalDateTime addedAt;
    // 非数据库字段：关联的动漫信息
    private String animeNameCn;
    private String animeNameJp;
    private String animeCoverUrl;
    private String animeBangumiScore;
    private String animeState;
    private Integer animeEpisode;
    private List<Tag> tags;
}
