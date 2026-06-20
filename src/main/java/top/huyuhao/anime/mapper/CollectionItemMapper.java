package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.CollectionItem;

import java.util.List;

@Mapper
public interface CollectionItemMapper {

    @Insert("insert into collection_item(collection_id, anime_id) values (#{collectionId}, #{animeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CollectionItem item);

    @Delete("delete from collection_item where id = #{id}")
    void delete(Integer id);

    @Delete("delete from collection_item where collection_id = #{collectionId} and anime_id = #{animeId}")
    void deleteByCollectionAndAnime(@Param("collectionId") Integer collectionId,
                                     @Param("animeId") Integer animeId);

    // 移动动漫：更新 collection_id
    @Update("update collection_item set collection_id = #{toCollectionId} " +
            "where collection_id = #{fromCollectionId} and anime_id = #{animeId}")
    void moveAnime(@Param("fromCollectionId") Integer fromCollectionId,
                   @Param("toCollectionId") Integer toCollectionId,
                   @Param("animeId") Integer animeId);

    @Select("select ci.*, a.name_cn as anime_name_cn, a.cover_url as anime_cover_url, " +
            "a.state as anime_state, a.episode as anime_episode " +
            "from collection_item ci inner join anime a on ci.anime_id = a.id " +
            "where ci.collection_id = #{collectionId} order by ci.added_at desc")
    @Results({
            @Result(property = "collectionId", column = "collection_id"),
            @Result(property = "animeId", column = "anime_id"),
            @Result(property = "addedAt", column = "added_at"),
            @Result(property = "animeNameCn", column = "anime_name_cn"),
            @Result(property = "animeCoverUrl", column = "anime_cover_url"),
            @Result(property = "animeState", column = "anime_state"),
            @Result(property = "animeEpisode", column = "anime_episode")
    })
    List<CollectionItem> findByCollectionId(Integer collectionId);

    @Select("select count(*) from collection_item where collection_id = #{collectionId}")
    Integer countByCollectionId(Integer collectionId);

    @Select("select * from collection_item where collection_id = #{collectionId} and anime_id = #{animeId} limit 1")
    @Results({
            @Result(property = "collectionId", column = "collection_id"),
            @Result(property = "animeId", column = "anime_id"),
            @Result(property = "addedAt", column = "added_at")
    })
    CollectionItem findByCollectionAndAnime(@Param("collectionId") Integer collectionId,
                                             @Param("animeId") Integer animeId);
}
