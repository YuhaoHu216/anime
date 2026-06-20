package top.huyuhao.anime.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.huyuhao.anime.mapper.AnimeMapper;
import top.huyuhao.anime.mapper.TagMapper;
import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.Tag;
import top.huyuhao.anime.service.AnimeService;

import java.util.List;

@Service
public class AnimeServiceImpl implements AnimeService {

    @Autowired
    private AnimeMapper animeMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public PageBean<Anime> search(Integer page, Integer pageSize, String name, String state, Integer tagId) {
        PageHelper.startPage(page, pageSize);
        List<Anime> animeList = animeMapper.search(name, state, tagId);
        // 为每个动漫加载标签
        for (Anime anime : animeList) {
            anime.setTags(tagMapper.findByAnimeId(anime.getId()));
        }
        Page<Anime> p = (Page<Anime>) animeList;
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    public Anime findById(Integer id) {
        Anime anime = animeMapper.findById(id);
        if (anime != null) {
            anime.setTags(tagMapper.findByAnimeId(id));
        }
        return anime;
    }

    @Override
    @Transactional
    public Result addAnime(Anime anime, List<Integer> tagIds) {
        anime.setReviewStatus("approved");
        animeMapper.insert(anime);
        // 关联标签
        if (tagIds != null) {
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success();
    }

    @Override
    @Transactional
    public void updateAnime(Anime anime, List<Integer> tagIds) {
        animeMapper.update(anime);
        if (tagIds != null) {
            tagMapper.unlinkAllTags(anime.getId());
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAnime(Integer id) {
        tagMapper.unlinkAllTags(id);
        animeMapper.delete(id);
    }

    @Override
    @Transactional
    public void submitAnime(Anime anime, List<Integer> tagIds, Integer userId) {
        anime.setReviewStatus("pending");
        anime.setSubmittedBy(userId);
        animeMapper.insert(anime);
        if (tagIds != null) {
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
    }
}
