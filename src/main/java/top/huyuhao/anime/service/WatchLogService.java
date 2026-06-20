package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.WatchLog;

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
}
