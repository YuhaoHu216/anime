package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.Comment;
import top.huyuhao.anime.pojo.CommentLike;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    @Insert("insert into comment(anime_id, user_id, parent_id, reply_to_user_id, content, images) " +
            "values (#{animeId}, #{userId}, #{parentId}, #{replyToUserId}, #{content}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comment comment);

    @Delete("delete from comment where id = #{id}")
    void deleteById(Integer id);

    @Update("update comment set like_count = like_count + 1 where id = #{id}")
    void incrLike(Integer id);

    @Update("update comment set like_count = like_count - 1 where id = #{id}")
    void decrLike(Integer id);

    @Update("update comment set dislike_count = dislike_count + 1 where id = #{id}")
    void incrDislike(Integer id);

    @Update("update comment set dislike_count = dislike_count - 1 where id = #{id}")
    void decrDislike(Integer id);

    @Insert("insert into comment_like(comment_id, user_id, type) values (#{commentId}, #{userId}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertLike(CommentLike commentLike);

    @Delete("delete from comment_like where comment_id = #{commentId} and user_id = #{userId}")
    void deleteLike(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

    @Update("update comment_like set type = #{type} where comment_id = #{commentId} and user_id = #{userId}")
    void updateLikeType(@Param("commentId") Integer commentId, @Param("userId") Integer userId, @Param("type") Integer type);

    @Select("select * from comment_like where comment_id = #{commentId} and user_id = #{userId}")
    CommentLike findLike(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

    // 以下查询在 CommentMapper.xml 中定义（join user 表带出用户名/头像）
    Comment findById(Integer id);

    List<Comment> findTopLevelByAnime(Integer animeId);

    List<Comment> findChildrenByRootIds(@Param("rootIds") List<Integer> rootIds);

    List<Map<String, Object>> countChildrenByRootIds(@Param("rootIds") List<Integer> rootIds);

    List<Comment> findChildrenByRoot(Integer parentId);

    List<CommentLike> findLikesByCommentIds(@Param("commentIds") List<Integer> commentIds, @Param("userId") Integer userId);
}
