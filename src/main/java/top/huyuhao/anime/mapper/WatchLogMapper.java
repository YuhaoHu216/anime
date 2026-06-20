package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.WatchLog;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WatchLogMapper {

    @Insert("insert into watch_log(user_id, anime_id, watch_date, ep_start, ep_end, ep_count, notes) " +
            "values (#{userId}, #{animeId}, #{watchDate}, #{epStart}, #{epEnd}, #{epCount}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(WatchLog watchLog);

    @Update("update watch_log set watch_date = #{watchDate}, ep_start = #{epStart}, " +
            "ep_end = #{epEnd}, ep_count = #{epCount}, notes = #{notes} where id = #{id}")
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
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "animeNameCn", column = "anime_name_cn"),
            @Result(property = "animeCoverUrl", column = "anime_cover_url")
    })
    List<WatchLog> findByUserAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

    // 统计某动漫的总观看集数
    @Select("select coalesce(sum(ep_count), 0) from watch_log " +
            "where user_id = #{userId} and anime_id = #{animeId}")
    Integer getTotalEpisodes(@Param("userId") Integer userId, @Param("animeId") Integer animeId);
}
