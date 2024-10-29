package hu.news.mapper;

import hu.news.pojo.Anime;
import hu.news.pojo.AnimeWatched;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnimeMapper {

    List<Anime> search(String name,String state);

    //查询看番记录
    List<Anime> getAllWatched(String name,String table);

    //补番目录新增
    @Insert("insert  into unwatched(name,broadcastTime,bangumiScore,episode) values (#{name},#{broadcastTime},#{bangumiScore},#{episode})")
    void addAnime(Anime anime);

    //补番目录删除
    @Delete("delete from unwatched where id = ${id}")
    void deleteAnime(Integer id);

    //补番目录修改
    @Update("update unwatched set name = #{name},broadcastTime = #{broadcastTime}, bangumiScore = #{bangumiScore},episode=#{episode},state=#{state} where id = #{id}")
    void updateAnime(Anime anime);

    //观番记录新增
    @Insert("insert into ${table}(watchTime,name,broadcastTime,bangumi_score,episode) values (#{animeWatched.watchTime},#{animeWatched.name},#{animeWatched.broadcastTime},#{animeWatched.bangumiScore},#{animeWatched.episode})")
    void addWatched(AnimeWatched animeWatched,String table);

    //观番记录修改
    @Update("update ${table} set watchTime=#{watchTime},name=#{name},broadcastTime=#{broadcastTime},bangumi_score=#{bangumiScore},episode=#{episode} where name IS NULL")
    void updateWatched(AnimeWatched animeWatched, String table);
}
