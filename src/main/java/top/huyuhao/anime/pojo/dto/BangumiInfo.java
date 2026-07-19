package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bangumi 解析结果，用于返回给前端填充表单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bangumi 解析结果")
public class BangumiInfo {

    @Schema(description = "中文名", example = "命运石之门")
    private String nameCn;

    @Schema(description = "日文名", example = "STEINS;GATE")
    private String nameJp;

    @Schema(description = "播出时间", example = "2011-04-06")
    private String broadcastTime;

    @Schema(description = "Bangumi 评分", example = "8.7")
    private String bangumiScore;

    @Schema(description = "总集数", example = "24")
    private Integer episode;

    @Schema(description = "官方网址")
    private String officialWebsite;

    @Schema(description = "Bangumi 条目地址")
    private String bangumiUrl;

    @Schema(description = "动漫简介")
    private String description;

    @Schema(description = "Bangumi 封面图片原始 URL（需通过 /anime/proxy-image 下载）")
    private String coverUrl;
}
