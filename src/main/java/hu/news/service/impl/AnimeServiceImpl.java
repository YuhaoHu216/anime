package hu.news.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import hu.news.mapper.AnimeMapper;
import hu.news.pojo.Anime;
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
    public PageBean search(Integer page,Integer pageSize,String name){
        //设置分页参数
        PageHelper.startPage(page,pageSize);

        //执行查询
        List<Anime> animeList = animeMapper.search(name);

        //获取分页结果
        Page<Anime> p = (Page<Anime>) animeList;

        //封装PageBean对象
        PageBean pageBean = new PageBean(p.getTotal(),p.getResult());

        return pageBean;
    }

    @Override
    public PageBean getAll(Integer page,Integer pageSize) {
        PageHelper.startPage(page,pageSize);

        List<Anime> animeList = animeMapper.getAll();
        Page<Anime> p = (Page<Anime>) animeList;
        PageBean pageBean = new PageBean(p.getTotal(),p.getResult());
        return pageBean;
    }


}
