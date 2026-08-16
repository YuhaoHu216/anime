package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.huyuhao.anime.pojo.Anime;

import java.util.List;

/**
 * 修改动漫的请求 DTO，封装 anime 字段和 tagIds，id 必填。
 */
@Data
@Schema(description = "动漫修改请求体")
public class AnimeUpdateDTO {

    @Schema(description = "动漫ID", example = "123")
    private Integer id;

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

    @Schema(description = "官方网址", example = "https://shingeki.tv/")
    private String officialWebsite;

    @Schema(description = "Bangumi 地址", example = "https://bangumi.tv/subject/123456")
    private String bangumiUrl;

    @Schema(description = "动漫简介")
    private String description;

    @Schema(description = "关联标签 ID 列表", example = "[1, 2, 3]")
    private List<Integer> tagIds;

    @Schema(description = "剧集列表")
    private List<EpisodeDTO> episodes;

    /**
     * 将 DTO 转换为 Anime 实体对象
     */
    public Anime toAnime() {
        Anime anime = new Anime();
        anime.setId(this.id);
        anime.setNameCn(this.nameCn);
        anime.setNameJp(this.nameJp);
        anime.setBroadcastTime(this.broadcastTime);
        anime.setBangumiScore(this.bangumiScore);
        anime.setEpisode(this.episode);
        anime.setCoverUrl(this.coverUrl);
        anime.setOfficialWebsite(this.officialWebsite);
        anime.setBangumiUrl(this.bangumiUrl);
        anime.setDescription(this.description);
        return anime;
    }
}
