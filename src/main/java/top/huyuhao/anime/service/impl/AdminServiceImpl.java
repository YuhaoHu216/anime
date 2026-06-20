package top.huyuhao.anime.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.huyuhao.anime.mapper.AnimeMapper;
import top.huyuhao.anime.mapper.TagMapper;
import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Tag;
import top.huyuhao.anime.service.AdminService;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AnimeMapper animeMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public PageBean<Anime> getPendingReviews(Integer page, Integer pageSize, String name) {
        PageHelper.startPage(page, pageSize);
        List<Anime> list = animeMapper.searchPending(name);
        Page<Anime> p = (Page<Anime>) list;
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    public void reviewAnime(Integer animeId, String reviewStatus, String reviewComment, Integer adminId) {
        animeMapper.review(animeId, reviewStatus, adminId, reviewComment);
    }

    @Override
    public Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public void deleteTag(Integer id) {
        tagMapper.delete(id);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagMapper.findAll();
    }
}
