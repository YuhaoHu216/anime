package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.huyuhao.anime.pojo.WatchLog;

import java.time.LocalDate;

/**
 * 更新追番记录请求 DTO，只暴露可修改的字段，id 由路径参数提供。
 */
@Data
@Schema(description = "更新追番记录请求体")
public class WatchLogUpdateDTO {

    @Schema(description = "动漫ID", example = "1")
    private Integer animeId;

    @Schema(description = "观看日期（yyyy-MM-dd）", example = "2025-01-01")
    private LocalDate watchDate;

    @Schema(description = "起始集数", example = "1")
    private Integer epStart;

    @Schema(description = "结束集数", example = "3")
    private Integer epEnd;

    @Schema(description = "观看集数", example = "3")
    private Integer epCount;

    @Schema(description = "观看集号明细，逗号分隔（含SP/OVA/OAD番外）", example = "1,2,3,SP")
    private String epNos;

    @Schema(description = "备注", example = "二刷了")
    private String notes;

    /**
     * 将 DTO 转换为 WatchLog 实体对象（不含 id，由调用方设置）
     */
    public WatchLog toWatchLog() {
        WatchLog watchLog = new WatchLog();
        watchLog.setAnimeId(this.animeId);
        watchLog.setWatchDate(this.watchDate);
        watchLog.setEpStart(this.epStart);
        watchLog.setEpEnd(this.epEnd);
        watchLog.setEpCount(this.epCount);
        watchLog.setEpNos(this.epNos);
        watchLog.setNotes(this.notes);
        return watchLog;
    }
}