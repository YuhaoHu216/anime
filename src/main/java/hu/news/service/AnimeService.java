package hu.news.service;

import hu.news.pojo.Anime;
import hu.news.pojo.AnimeWatched;
import hu.news.pojo.PageBean;
import org.springframework.stereotype.Service;

@Service
public interface AnimeService {
    //根据番名分页查询
    PageBean search(Integer page, Integer pageSize, String name,String state);

    //查询看番记录
    PageBean getAllWatched(Integer page, Integer pageSize, String name,Integer year);

    //补番目录新增
    void addAnime(Anime anime);

    //补番目录删除
    void deleteAnime(Integer id);

    //补番目录修改
    void updateAnime(Anime anime);

    //观番记录新增
    void addWatched(AnimeWatched animeWatched);

    //观番记录修改
    void updateWatched(AnimeWatched animeWatched);
}
