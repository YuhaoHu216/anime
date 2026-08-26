package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Comment;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.dto.CommentAddDTO;

import java.util.List;
import java.util.Map;

public interface CommentService {

    /**
     * 分页查询顶层评论，每条附带最近 N 条子评论、子评论总数、当前用户点赞状态
     */
    PageBean<Comment> list(Integer animeId, Integer page, Integer pageSize, Integer currentUserId);

    /**
     * 展开某顶层评论的全部子评论
     */
    List<Comment> children(Integer rootId, Integer currentUserId);

    /**
     * 发表评论（顶层或子评论）
     */
    Comment add(CommentAddDTO dto, Integer userId);

    /**
     * 点赞/点踩（切换式），返回最新计数与当前用户状态
     */
    Map<String, Object> like(Integer commentId, Integer type, Integer userId);

    /**
     * 删除评论（作者本人或管理员）
     */
    void delete(Integer id, Integer currentUserId);
}
