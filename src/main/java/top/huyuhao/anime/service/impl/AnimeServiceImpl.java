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
    public Result findById(Integer id) {
        Anime anime = animeMapper.findById(id);
        if (anime == null) {
            throw new RuntimeException("动漫不存在");
        }
        anime.setTags(tagMapper.findByAnimeId(id));
        return Result.success(anime);
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
    public Result updateAnime(Anime anime, List<Integer> tagIds) {
        animeMapper.update(anime);
        if (tagIds != null) {
            tagMapper.unlinkAllTags(anime.getId());
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success("更新成功");
    }

    @Override
    @Transactional
    public Result deleteAnime(Integer id) {
        tagMapper.unlinkAllTags(id);
        animeMapper.delete(id);
        return Result.success("删除成功");
    }

    @Override
    @Transactional
    public Result submitAnime(Anime anime, List<Integer> tagIds, Integer userId) {
        anime.setReviewStatus("pending");
        anime.setSubmittedBy(userId);
        animeMapper.insert(anime);
        if (tagIds != null) {
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success("提交成功，等待管理员审核");
    }
}
