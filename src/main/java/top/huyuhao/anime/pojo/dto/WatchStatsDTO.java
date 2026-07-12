package top.huyuhao.anime.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 追番统计数据 —— 首页 Dashboard 统计卡片 + 热力图数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchStatsDTO {

    /** 累计追番去重动漫数 */
    private Integer totalAnime;

    /** 累计观看总集数 */
    private Integer totalEpisodes;

    /** 当前自然月观看集数 */
    private Integer monthEpisodes;

    /** 连续追番天数 */
    private Integer streakDays;

    /** 每日集数统计（热力图数据源） */
    private List<DailyStats> dailyStats;

    /**
     * 单日集数统计
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private String date;
        private Integer count;
    }
}
