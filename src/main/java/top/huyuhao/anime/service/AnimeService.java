package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;

import java.util.List;

public interface AnimeService {

    PageBean<Anime> search(Integer page, Integer pageSize, String name, String state, Integer tagId);

    Result findById(Integer id);

    /**
     * 预分配 animeId：插入占位记录并返回自增ID
     */
    Integer prepareAnime();

    Result addAnime(Anime anime, List<Integer> tagIds);

    Result updateAnime(Anime anime, List<Integer> tagIds);

    Result deleteAnime(Integer id);

    Result submitAnime(Anime anime, List<Integer> tagIds, Integer userId);
}
