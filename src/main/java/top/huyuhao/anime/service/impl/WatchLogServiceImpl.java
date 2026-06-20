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
import top.huyuhao.anime.pojo.WatchLog;
import top.huyuhao.anime.service.WatchLogService;

import java.time.LocalDate;
import java.util.List;

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
    public void addLog(WatchLog watchLog) {
        if (watchLog.getEpCount() == null) {
            watchLog.setEpCount(1);
        }
        watchLogMapper.insert(watchLog);
        // 记录观看后，自动将动漫加入"看过"默认收藏夹
        addToDefaultCollection(watchLog.getUserId(), watchLog.getAnimeId(), "看过");
    }

    @Override
    public void updateLog(WatchLog watchLog) {
        watchLogMapper.update(watchLog);
    }

    @Override
    public void deleteLog(Integer id) {
        watchLogMapper.delete(id);
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
