package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;

import java.util.List;

public interface AnimeService {

    PageBean<Anime> search(Integer page, Integer pageSize, String name, String state, Integer tagId);

    Anime findById(Integer id);

    Result addAnime(Anime anime, List<Integer> tagIds);

    void updateAnime(Anime anime, List<Integer> tagIds);

    void deleteAnime(Integer id);

    void submitAnime(Anime anime, List<Integer> tagIds, Integer userId);
}
