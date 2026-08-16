package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.WatchLog;
import top.huyuhao.anime.pojo.dto.WatchProgressDTO;
import top.huyuhao.anime.pojo.dto.WatchStatsDTO;

import java.time.LocalDate;
import java.util.List;

public interface WatchLogService {

    Result addLog(WatchLog watchLog);

    Result updateLog(WatchLog watchLog);

    Result deleteLog(Integer id);

    PageBean<WatchLog> getLogs(Integer page, Integer pageSize, Integer userId,
                               Integer animeId, LocalDate startDate, LocalDate endDate);

    List<WatchLog> getLogsByDate(Integer userId, LocalDate date);

    List<LocalDate> getCalendar(Integer userId, Integer year, Integer month);

    /**
     * 获取首页统计数据（统计卡片 + 热力图）
     */
    WatchStatsDTO getStats(Integer userId, LocalDate dailyStartDate, LocalDate dailyEndDate);

    /**
     * 获取最近N条追番记录
     */
    List<WatchLog> getRecentLogs(Integer userId, Integer limit);

    /**
     * 获取某动漫的追番进度（数字正片进度 + 已看集号明细，含番外）
     */
    WatchProgressDTO getProgress(Integer userId, Integer animeId);
}
