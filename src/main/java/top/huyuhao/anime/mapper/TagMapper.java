package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.Tag;

import java.util.List;

@Mapper
public interface TagMapper {

    @Select("select * from tag order by id")
    List<Tag> findAll();

    @Select("select t.* from tag t inner join anime_tag at on t.id = at.tag_id where at.anime_id = #{animeId}")
    List<Tag> findByAnimeId(Integer animeId);

    @Insert("insert into tag(name) values (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Tag tag);

    @Delete("delete from tag where id = #{id}")
    void delete(Integer id);

    // 关联操作
    @Insert("insert into anime_tag(anime_id, tag_id) values (#{animeId}, #{tagId})")
    void linkTag(@Param("animeId") Integer animeId, @Param("tagId") Integer tagId);

    @Delete("delete from anime_tag where anime_id = #{animeId}")
    void unlinkAllTags(Integer animeId);

    @Delete("delete from anime_tag where anime_id = #{animeId} and tag_id = #{tagId}")
    void unlinkTag(@Param("animeId") Integer animeId, @Param("tagId") Integer tagId);
}
