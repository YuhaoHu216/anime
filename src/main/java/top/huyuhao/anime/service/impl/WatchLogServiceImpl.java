package top.huyuhao.anime.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.huyuhao.anime.mapper.CollectionItemMapper;
import top.huyuhao.anime.mapper.CollectionMapper;
import top.huyuhao.anime.mapper.WatchLogMapper;
import top.huyuhao.anime.pojo.Collection;
import top.huyuhao.anime.pojo.CollectionItem;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.WatchLog;
import top.huyuhao.anime.service.WatchLogService;
import top.huyuhao.anime.pojo.dto.WatchProgressDTO;
import top.huyuhao.anime.pojo.dto.WatchStatsDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WatchLogServiceImpl implements WatchLogService {

    @Autowired
    private WatchLogMapper watchLogMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionItemMapper collectionItemMapper;

    @Override
    @Transactional
    public Result addLog(WatchLog watchLog) {
        if (watchLog.getEpCount() == null) {
            watchLog.setEpCount(1);
        }
        watchLogMapper.insert(watchLog);
        // 记录观看后，自动将动漫加入"看过"默认收藏夹
        addToDefaultCollection(watchLog.getUserId(), watchLog.getAnimeId(), "看过");
        return Result.success("记录成功");
    }

    @Override
    public Result updateLog(WatchLog watchLog) {
        watchLogMapper.update(watchLog);
        return Result.success("更新成功");
    }

    @Override
    public Result deleteLog(Integer id) {
        watchLogMapper.delete(id);
        return Result.success("删除成功");
    }

    @Override
    public PageBean<WatchLog> getLogs(Integer page, Integer pageSize, Integer userId,
                                       Integer animeId, LocalDate startDate, LocalDate endDate) {
        PageHelper.startPage(page, pageSize);
        List<WatchLog> logs = watchLogMapper.findByUser(userId, animeId, startDate, endDate);
        Page<WatchLog> p = (Page<WatchLog>) logs;
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    public List<WatchLog> getLogsByDate(Integer userId, LocalDate date) {
        return watchLogMapper.findByUserAndDate(userId, date);
    }

    @Override
    public List<LocalDate> getCalendar(Integer userId, Integer year, Integer month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return watchLogMapper.findWatchDates(userId, startDate, endDate);
    }

    @Override
    public WatchStatsDTO getStats(Integer userId, LocalDate dailyStartDate, LocalDate dailyEndDate) {
        WatchStatsDTO dto = new WatchStatsDTO();

        // 统计卡片
        dto.setTotalAnime(watchLogMapper.countDistinctAnime(userId));
        dto.setTotalEpisodes(watchLogMapper.sumEpisodes(userId));

        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        dto.setMonthEpisodes(watchLogMapper.sumMonthEpisodes(userId, monthStart, now));

        // 连续天数
        List<LocalDate> distinctDates = watchLogMapper.getDistinctWatchDates(userId);
        dto.setStreakDays(calcStreakDays(distinctDates, now));

        // 每日集数（热力图）
        List<Map<String, Object>> rawDaily = watchLogMapper.getDailyStats(userId, dailyStartDate, dailyEndDate);
        List<WatchStatsDTO.DailyStats> dailyStats = new java.util.ArrayList<>();
        if (rawDaily != null) {
            for (Map<String, Object> row : rawDaily) {
                WatchStatsDTO.DailyStats ds = new WatchStatsDTO.DailyStats();
                ds.setDate((String) row.get("date"));
                ds.setCount(((Number) row.get("count")).intValue());
                dailyStats.add(ds);
            }
        }
        dto.setDailyStats(dailyStats);

        return dto;
    }

    @Override
    public List<WatchLog> getRecentLogs(Integer userId, Integer limit) {
        return watchLogMapper.getRecentLogs(userId, limit);
    }

    @Override
    public WatchProgressDTO getProgress(Integer userId, Integer animeId) {
        Integer maxEpEnd = watchLogMapper.getMaxEpEnd(userId, animeId);
        List<String> watchedEpNos = new java.util.ArrayList<>();
        List<String> epNosList = watchLogMapper.findEpNosByUserAndAnime(userId, animeId);
        if (epNosList != null) {
            java.util.Set<String> set = new java.util.LinkedHashSet<>();
            for (String epNos : epNosList) {
                if (epNos != null) {
                    for (String no : epNos.split(",")) {
                        String t = no.trim();
                        if (!t.isEmpty()) {
                            set.add(t);
                        }
                    }
                }
            }
            watchedEpNos.addAll(set);
        }
        return new WatchProgressDTO(maxEpEnd, watchedEpNos);
    }

    /**
     * 计算连续追番天数：从今天开始向前数，遇到中断即停止。
     * 如果今天没有记录，则从昨天开始算。
     */
    private int calcStreakDays(List<LocalDate> distinctDates, LocalDate today) {
        if (distinctDates == null || distinctDates.isEmpty()) return 0;
        java.util.Set<LocalDate> dateSet = new java.util.HashSet<>(distinctDates);
        int streak = 0;
        LocalDate cursor = dateSet.contains(today) ? today : today.minusDays(1);
        while (dateSet.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private void addToDefaultCollection(Integer userId, Integer animeId, String collectionName) {
        Collection watched = collectionMapper.findDefaultByName(userId, collectionName);
        if (watched != null) {
            CollectionItem existing = collectionItemMapper.findByCollectionAndAnime(watched.getId(), animeId);
            if (existing == null) {
                CollectionItem item = new CollectionItem();
                item.setCollectionId(watched.getId());
                item.setAnimeId(animeId);
                collectionItemMapper.insert(item);
            }
        }
    }
}
