package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.huyuhao.anime.pojo.Episode;

import java.util.List;

@Mapper
public interface EpisodeMapper {

    void batchInsert(@Param("animeId") Integer animeId, @Param("list") List<Episode> list);

    @Delete("delete from episode where anime_id = #{animeId}")
    void deleteByAnimeId(@Param("animeId") Integer animeId);

    @Select("select * from episode where anime_id = #{animeId} order by sort asc, id asc")
    List<Episode> findByAnimeId(@Param("animeId") Integer animeId);
}
