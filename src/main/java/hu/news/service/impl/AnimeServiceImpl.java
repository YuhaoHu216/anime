package hu.news.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import hu.news.mapper.AnimeMapper;
import hu.news.pojo.Anime;
import hu.news.pojo.AnimeWatched;
import hu.news.pojo.PageBean;
import hu.news.service.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimeServiceImpl implements AnimeService {
    @Autowired
    private AnimeMapper animeMapper;

    //根据番名分页查询
    @Override
    public PageBean search(Integer page,Integer pageSize,String name,String state){
        //设置分页参数
        PageHelper.startPage(page,pageSize);

        //执行查询
        List<Anime> animeList = animeMapper.search(name,state);

        //获取分页结果
        Page<Anime> p = (Page<Anime>) animeList;

        //封装PageBean对象
        PageBean pageBean = new PageBean(p.getTotal(),p.getResult());

        return pageBean;
    }

    //查询看番记录
    @Override
    public PageBean getAllWatched(Integer page, Integer pageSize, String name, Integer year) {
        PageHelper.startPage(page,pageSize);
        String table = "watched" + year;
        List<Anime> animeList = animeMapper.getAllWatched(name,table);
        Page<Anime> p = (Page<Anime>) animeList;
        PageBean pageBean = new PageBean(p.getTotal(),p.getResult());
        return pageBean;
    }

    @Override
    public void addAnime(Anime anime) {
        animeMapper.addAnime(anime);
    }

    @Override
    public void deleteAnime(Integer id) {
        animeMapper.deleteAnime(id);
    }

    @Override
    public void updateAnime(Anime anime) {
        animeMapper.updateAnime(anime);
    }

    @Override
    public void addWatched(AnimeWatched animeWatched) {
        String table = "watched" + animeWatched.getYear();
        animeMapper.addWatched(animeWatched,table);
    }

    @Override
    public void updateWatched(AnimeWatched animeWatched) {
        String table = "watched" + animeWatched.getYear();
        animeMapper.updateWatched(animeWatched,table);
    }


}
