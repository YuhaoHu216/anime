package top.huyuhao.anime.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.huyuhao.anime.mapper.CommentMapper;
import top.huyuhao.anime.mapper.UserMapper;
import top.huyuhao.anime.pojo.Comment;
import top.huyuhao.anime.pojo.CommentLike;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.User;
import top.huyuhao.anime.pojo.dto.CommentAddDTO;
import top.huyuhao.anime.service.CommentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    /** 每条顶层评论默认展示的最近子评论条数 */
    private static final int RECENT_CHILD_N = 3;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public PageBean<Comment> list(Integer animeId, Integer page, Integer pageSize, Integer currentUserId) {
        PageHelper.startPage(page, pageSize);
        List<Comment> tops = commentMapper.findTopLevelByAnime(animeId);
        Page<Comment> p = (Page<Comment>) tops;

        if (!tops.isEmpty()) {
            List<Integer> rootIds = tops.stream().map(Comment::getId).collect(Collectors.toList());

            // 子评论总数
            Map<Integer, Integer> cntMap = new HashMap<>();
            for (Map<String, Object> row : commentMapper.countChildrenByRootIds(rootIds)) {
                cntMap.put(((Number) row.get("rootId")).intValue(), ((Number) row.get("cnt")).intValue());
            }

            // 全部子评论 → 每个根取最近 N 条（XML 已按 created_at desc 排序）
            List<Comment> allChildren = commentMapper.findChildrenByRootIds(rootIds);
            Map<Integer, List<Comment>> childMap = new HashMap<>();
            for (Comment ch : allChildren) {
                childMap.computeIfAbsent(ch.getParentId(), k -> new ArrayList<>()).add(ch);
            }

            for (Comment top : tops) {
                top.setReplyCount(cntMap.getOrDefault(top.getId(), 0));
                List<Comment> recent = childMap.getOrDefault(top.getId(), new ArrayList<>())
                        .stream().limit(RECENT_CHILD_N).collect(Collectors.toList());
                Collections.reverse(recent);
                recent.forEach(this::fillImageList);
                top.setChildren(recent);
                fillImageList(top);
            }

            if (currentUserId != null) {
                fillMyTypes(tops, currentUserId);
            }
        }
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    public List<Comment> children(Integer rootId, Integer currentUserId) {
        List<Comment> list = commentMapper.findChildrenByRoot(rootId);
        list.forEach(this::fillImageList);
        if (currentUserId != null && !list.isEmpty()) {
            fillMyTypes(list, currentUserId);
        }
        return list;
    }

    @Override
    public Comment add(CommentAddDTO dto, Integer userId) {
        String content = dto.getContent() == null ? "" : dto.getContent().trim();
        List<String> images = dto.getImages();
        boolean hasImages = images != null && !images.isEmpty();
        if (content.isEmpty() && !hasImages) {
            throw new RuntimeException("评论内容不能为空");
        }
        if (hasImages && images.size() > 9) {
            throw new RuntimeException("最多上传9张图片");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent(content.isEmpty() ? null : content);
        comment.setImages(hasImages ? toJson(images) : null);
        comment.setReplyToUserId(dto.getReplyToUserId());

        if (dto.getParentId() != null) {
            Comment parent = commentMapper.findById(dto.getParentId());
            if (parent == null) {
                throw new RuntimeException("回复的评论不存在");
            }
            if (parent.getParentId() != null) {
                throw new RuntimeException("仅支持对一级评论回复");
            }
            comment.setParentId(parent.getId());
            comment.setAnimeId(parent.getAnimeId());
        } else {
            if (dto.getAnimeId() == null) {
                throw new RuntimeException("缺少动漫ID");
            }
            comment.setAnimeId(dto.getAnimeId());
        }

        commentMapper.insert(comment);
        return commentMapper.findById(comment.getId());
    }

    @Override
    @Transactional
    public Map<String, Object> like(Integer commentId, Integer type, Integer userId) {
        Comment comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (type == null || (type != 1 && type != -1)) {
            throw new RuntimeException("点赞类型不合法");
        }

        CommentLike existing = commentMapper.findLike(commentId, userId);
        if (existing == null) {
            // 无记录 → 新增
            CommentLike like = new CommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            like.setType(type);
            commentMapper.insertLike(like);
            if (type == 1) {
                commentMapper.incrLike(commentId);
            } else {
                commentMapper.incrDislike(commentId);
            }
        } else if (existing.getType().equals(type)) {
            // 同类型 → 取消
            commentMapper.deleteLike(commentId, userId);
            if (type == 1) {
                commentMapper.decrLike(commentId);
            } else {
                commentMapper.decrDislike(commentId);
            }
            type = 0;
        } else {
            // 不同类型 → 翻转
            commentMapper.updateLikeType(commentId, userId, type);
            if (type == 1) {
                commentMapper.decrDislike(commentId);
                commentMapper.incrLike(commentId);
            } else {
                commentMapper.decrLike(commentId);
                commentMapper.incrDislike(commentId);
            }
        }

        Comment latest = commentMapper.findById(commentId);
        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", latest.getLikeCount());
        result.put("dislikeCount", latest.getDislikeCount());
        result.put("myType", type);
        return result;
    }

    @Override
    public void delete(Integer id, Integer currentUserId) {
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        boolean isAuthor = comment.getUserId().equals(currentUserId);
        boolean isAdmin = false;
        if (!isAuthor) {
            User user = userMapper.findById(currentUserId);
            isAdmin = user != null && "admin".equals(user.getRole());
        }
        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("无权删除该评论");
        }
        commentMapper.deleteById(id);
    }

    /** 批量回填当前用户对顶层及子评论的点赞状态（myType），整页一次查询 */
    private void fillMyTypes(List<Comment> comments, Integer userId) {
        List<Integer> ids = new ArrayList<>();
        for (Comment c : comments) {
            ids.add(c.getId());
            if (c.getChildren() != null) {
                for (Comment ch : c.getChildren()) {
                    ids.add(ch.getId());
                }
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        Map<Integer, Integer> typeMap = new HashMap<>();
        for (CommentLike like : commentMapper.findLikesByCommentIds(ids, userId)) {
            typeMap.put(like.getCommentId(), like.getType());
        }
        for (Comment c : comments) {
            c.setMyType(typeMap.getOrDefault(c.getId(), 0));
            if (c.getChildren() != null) {
                for (Comment ch : c.getChildren()) {
                    ch.setMyType(typeMap.getOrDefault(ch.getId(), 0));
                }
            }
        }
    }

    /** 解析 images JSON 数组字符串为 List，空/非法置空列表 */
    private void fillImageList(Comment comment) {
        if (comment.getImages() == null || comment.getImages().isBlank()) {
            comment.setImageList(new ArrayList<>());
            return;
        }
        try {
            comment.setImageList(om.readValue(comment.getImages(), new TypeReference<List<String>>() {}));
        } catch (Exception e) {
            comment.setImageList(new ArrayList<>());
        }
    }

    private String toJson(List<String> list) {
        try {
            return om.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("图片数据格式错误", e);
        }
    }
}
