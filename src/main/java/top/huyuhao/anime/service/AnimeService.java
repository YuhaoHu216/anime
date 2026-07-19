package top.huyuhao.anime.service;

import org.springframework.core.io.Resource;
import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.dto.BangumiInfo;

import java.util.List;

public interface AnimeService {

    PageBean<Anime> search(Integer page, Integer pageSize, String name, Integer tagId);

    Result findById(Integer id);

    /**
     * 预分配 animeId：插入占位记录并返回自增ID
     */
    Integer prepareAnime();

    Result addAnime(Anime anime, List<Integer> tagIds);

    Result updateAnime(Anime anime, List<Integer> tagIds);

    Result deleteAnime(Integer id);

    Result submitAnime(Anime anime, List<Integer> tagIds, Integer userId);

    PageBean<Anime> getMySubmissions(Integer page, Integer pageSize, String reviewStatus, Integer userId);

    /**
     * 解析 Bangumi 地址，从 Bangumi API 获取动漫信息
     */
    BangumiInfo parseBangumiInfo(String bangumiUrl);

    /**
     * 代理下载图片，返回图片资源
     */
    Resource proxyImage(String imageUrl);
}
