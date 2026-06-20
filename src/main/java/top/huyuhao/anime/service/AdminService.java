package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.Tag;

import java.util.List;

public interface AdminService {

    // 动漫审核
    PageBean<Anime> getPendingReviews(Integer page, Integer pageSize, String name);

    Result reviewAnime(Integer animeId, String reviewStatus, String reviewComment, Integer adminId);

    // 标签管理
    Result createTag(String name);

    Result deleteTag(Integer id);

    List<Tag> getAllTags();
}
