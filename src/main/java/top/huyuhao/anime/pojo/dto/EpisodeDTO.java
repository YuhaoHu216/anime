package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.huyuhao.anime.pojo.Episode;

@Data
@Schema(description = "剧集信息")
public class EpisodeDTO {

    @Schema(description = "集号", example = "1")
    private String epNo;

    @Schema(description = "集名", example = "致两千年后的你")
    private String name;

    @Schema(description = "放送日期", example = "2026-04-05")
    private String airDate;

    @Schema(description = "时长（总秒数）", example = "1470")
    private Integer duration;

    @Schema(description = "排序号", example = "1")
    private Double sort;

    /**
     * 将 DTO 转换为 Episode 实体对象
     */
    public Episode toEpisode() {
        Episode e = new Episode();
        e.setEpNo(this.epNo);
        e.setName(this.name);
        e.setAirDate(this.airDate);
        e.setDuration(this.duration);
        e.setSort(this.sort);
        return e;
    }
}
