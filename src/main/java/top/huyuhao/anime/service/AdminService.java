package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Tag;

import java.util.List;

public interface AdminService {

    // 动漫审核
    PageBean<Anime> getPendingReviews(Integer page, Integer pageSize, String name);

    void reviewAnime(Integer animeId, String reviewStatus, String reviewComment, Integer adminId);

    // 标签管理
    Tag createTag(String name);

    void deleteTag(Integer id);

    List<Tag> getAllTags();
}
