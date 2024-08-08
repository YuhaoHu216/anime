package hu.news.service;

import hu.news.pojo.PageBean;
import org.springframework.stereotype.Service;

@Service
public interface AnimeService {
    //根据番名分页查询
    PageBean search(Integer page, Integer pageSize, String name);

    //显示全部番
    PageBean getAll(Integer page, Integer pageSize);
}
