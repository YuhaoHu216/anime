package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.Anime;

import java.util.List;

@Mapper
public interface AnimeMapper {

    // 搜索已审核通过的动漫（分页由 PageHelper 处理）
    List<Anime> search(@Param("name") String name,
                       @Param("tagId") Integer tagId);

    @Select("select * from anime where id = #{id}")
    @Results({
            @Result(property = "nameCn", column = "name_cn"),
            @Result(property = "nameJp", column = "name_jp"),
            @Result(property = "broadcastTime", column = "broadcast_time"),
            @Result(property = "bangumiScore", column = "bangumi_score"),
            @Result(property = "coverUrl", column = "cover_url"),
            @Result(property = "officialWebsite", column = "official_website"),
            @Result(property = "reviewStatus", column = "review_status"),
            @Result(property = "submittedBy", column = "submitted_by"),
            @Result(property = "reviewedBy", column = "reviewed_by"),
            @Result(property = "reviewComment", column = "review_comment"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Anime findById(Integer id);

    @Insert("insert into anime(name_cn, name_jp, broadcast_time, bangumi_score, episode, " +
            "cover_url, official_website, description, review_status, submitted_by) " +
            "values (#{nameCn}, #{nameJp}, #{broadcastTime}, #{bangumiScore}, #{episode}, " +
            "#{coverUrl}, #{officialWebsite}, #{description}, #{reviewStatus}, #{submittedBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Anime anime);

    @Insert("insert into anime(review_status) values ('preparing')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void prepareInsert(Anime anime);

    @Update("update anime set name_cn = #{nameCn}, name_jp = #{nameJp}, " +
            "broadcast_time = #{broadcastTime}, bangumi_score = #{bangumiScore}, " +
            "episode = #{episode}, cover_url = #{coverUrl}, " +
            "official_website = #{officialWebsite}, " +
            "description = #{description} where id = #{id}")
    void update(Anime anime);

    @Delete("delete from anime where id = #{id}")
    void delete(Integer id);

    // 审核相关
    @Update("update anime set review_status = #{reviewStatus}, reviewed_by = #{reviewedBy}, " +
            "review_comment = #{reviewComment} where id = #{id}")
    void review(@Param("id") Integer id,
                @Param("reviewStatus") String reviewStatus,
                @Param("reviewedBy") Integer reviewedBy,
                @Param("reviewComment") String reviewComment);

    // 审核列表（分页由 PageHelper 处理），reviewStatus 为空时查全部（排除 preparing）
    List<Anime> searchPending(@Param("name") String name,
                             @Param("reviewStatus") String reviewStatus);

    // 查询当前用户的提交记录（分页由 PageHelper 处理）
    List<Anime> searchBySubmitter(@Param("submittedBy") Integer submittedBy,
                                  @Param("reviewStatus") String reviewStatus);
}
