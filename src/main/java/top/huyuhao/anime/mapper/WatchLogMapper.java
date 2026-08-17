package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.WatchLog;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WatchLogMapper {

    @Insert("insert into watch_log(user_id, anime_id, watch_date, ep_start, ep_end, ep_count, ep_nos, notes) " +
            "values (#{userId}, #{animeId}, #{watchDate}, #{epStart}, #{epEnd}, #{epCount}, #{epNos}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(WatchLog watchLog);

    @Update("update watch_log set watch_date = #{watchDate}, ep_start = #{epStart}, " +
            "ep_end = #{epEnd}, ep_count = #{epCount}, ep_nos = #{epNos}, notes = #{notes} where id = #{id}")
    void update(WatchLog watchLog);

    @Delete("delete from watch_log where id = #{id}")
    void delete(Integer id);

    // 按用户+日期范围查询观看日志
    List<WatchLog> findByUser(@Param("userId") Integer userId,
                              @Param("animeId") Integer animeId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    // 日历模式：某月的观看天数
    @Select("select distinct watch_date from watch_log " +
            "where user_id = #{userId} and watch_date between #{startDate} and #{endDate} " +
            "order by watch_date")
    List<LocalDate> findWatchDates(@Param("userId") Integer userId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    // 某天某用户的观看记录
    @Select("select wl.*, a.name_cn as anime_name_cn, a.cover_url as anime_cover_url " +
            "from watch_log wl inner join anime a on wl.anime_id = a.id " +
            "where wl.user_id = #{userId} and wl.watch_date = #{date} " +
            "order by wl.created_at desc")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "animeId", column = "anime_id"),
            @Result(property = "watchDate", column = "watch_date"),
            @Result(property = "epStart", column = "ep_start"),
            @Result(property = "epEnd", column = "ep_end"),
            @Result(property = "epCount", column = "ep_count"),
            @Result(property = "epNos", column = "ep_nos"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "animeNameCn", column = "anime_name_cn"),
            @Result(property = "animeCoverUrl", column = "anime_cover_url")
    })
    List<WatchLog> findByUserAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

    // 统计某动漫的总观看集数
    @Select("select coalesce(sum(ep_count), 0) from watch_log " +
            "where user_id = #{userId} and anime_id = #{animeId}")
    Integer getTotalEpisodes(@Param("userId") Integer userId, @Param("animeId") Integer animeId);

    // 某用户某动漫的追番进度（已看到的集数 = 最大结束集）
    @Select("select coalesce(max(ep_end), 0) from watch_log " +
            "where user_id = #{userId} and anime_id = #{animeId}")
    Integer getMaxEpEnd(@Param("userId") Integer userId, @Param("animeId") Integer animeId);

    // 某用户某动漫所有观看记录的集号明细（非空，用于进度合并）
    @Select("select ep_nos from watch_log " +
            "where user_id = #{userId} and anime_id = #{animeId} and ep_nos is not null and ep_nos != ''")
    List<String> findEpNosByUserAndAnime(@Param("userId") Integer userId, @Param("animeId") Integer animeId);

    // 某用户某动漫所有观看记录的集号明细 + 观看日期（按日期升序，用于进度时间展示）
    @Select("select ep_nos, watch_date from watch_log " +
            "where user_id = #{userId} and anime_id = #{animeId} " +
            "and ep_nos is not null and ep_nos != '' " +
            "order by watch_date")
    List<java.util.Map<String, Object>> findEpNosWithDatesByUserAndAnime(@Param("userId") Integer userId,
                                                                         @Param("animeId") Integer animeId);

    // ============ 首页统计专用 ============

    /** 累计追番去重动漫数 */
    @Select("select count(distinct anime_id) from watch_log where user_id = #{userId}")
    Integer countDistinctAnime(@Param("userId") Integer userId);

    /** 累计总集数 */
    @Select("select coalesce(sum(ep_count), 0) from watch_log where user_id = #{userId}")
    Integer sumEpisodes(@Param("userId") Integer userId);

    /** 当月集数 */
    @Select("select coalesce(sum(ep_count), 0) from watch_log " +
            "where user_id = #{userId} and watch_date between #{startDate} and #{endDate}")
    Integer sumMonthEpisodes(@Param("userId") Integer userId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    /** 用户所有追番日期（降序，用于计算连续天数） */
    @Select("select distinct watch_date from watch_log " +
            "where user_id = #{userId} order by watch_date desc")
    List<LocalDate> getDistinctWatchDates(@Param("userId") Integer userId);

    /** 每日集数统计（热力图数据源） */
    @Select("select date_format(watch_date, '%Y-%m-%d') as `date`, sum(ep_count) as `count` " +
            "from watch_log " +
            "where user_id = #{userId} and watch_date between #{startDate} and #{endDate} " +
            "group by watch_date order by watch_date")
    List<java.util.Map<String, Object>> getDailyStats(@Param("userId") Integer userId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /** 最近N条追番记录（含动漫名称和封面） */
    @Select("select wl.*, a.name_cn as anime_name_cn, a.cover_url as anime_cover_url " +
            "from watch_log wl inner join anime a on wl.anime_id = a.id " +
            "where wl.user_id = #{userId} " +
            "order by wl.watch_date desc, wl.created_at desc " +
            "limit #{limit}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "animeId", column = "anime_id"),
            @Result(property = "watchDate", column = "watch_date"),
            @Result(property = "epStart", column = "ep_start"),
            @Result(property = "epEnd", column = "ep_end"),
            @Result(property = "epCount", column = "ep_count"),
            @Result(property = "epNos", column = "ep_nos"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "animeNameCn", column = "anime_name_cn"),
            @Result(property = "animeCoverUrl", column = "anime_cover_url")
    })
    List<WatchLog> getRecentLogs(@Param("userId") Integer userId, @Param("limit") Integer limit);
}
