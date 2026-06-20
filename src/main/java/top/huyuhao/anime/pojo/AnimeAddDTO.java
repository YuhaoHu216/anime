package top.huyuhao.anime.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 添加/修改动漫的请求 DTO，封装 anime 字段和 tagIds，避免 Controller 参数过多。
 * 对应 multipart/form-data 中的 "anime" JSON 部分。
 */
@Data
@Schema(description = "动漫添加/修改请求体")
public class AnimeAddDTO {

    @Schema(description = "中文名", example = "进击的巨人")
    private String nameCn;

    @Schema(description = "日文名", example = "進撃の巨人")
    private String nameJp;

    @Schema(description = "播出时间", example = "2013-04")
    private String broadcastTime;

    @Schema(description = "Bangumi 评分", example = "8.5")
    private String bangumiScore;

    @Schema(description = "总集数", example = "25")
    private Integer episode;

    @Schema(description = "封面图片 URL（一般由文件上传后回填）")
    private String coverUrl;

    @Schema(description = "动漫简介")
    private String description;

    @Schema(description = "关联标签 ID 列表", example = "[1, 2, 3]")
    private List<Integer> tagIds;

    /**
     * 将 DTO 转换为 Anime 实体对象（不包含 id / reviewStatus 等托管字段）
     */
    public Anime toAnime() {
        Anime anime = new Anime();
        anime.setNameCn(this.nameCn);
        anime.setNameJp(this.nameJp);
        anime.setBroadcastTime(this.broadcastTime);
        anime.setBangumiScore(this.bangumiScore);
        anime.setEpisode(this.episode);
        anime.setCoverUrl(this.coverUrl);
        anime.setDescription(this.description);
        return anime;
    }
}
