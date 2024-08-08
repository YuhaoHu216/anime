package hu.news.controller;

import hu.news.pojo.PageBean;
import hu.news.pojo.Result;
import hu.news.service.AnimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    //分页番名查询功能
    @CrossOrigin
    @GetMapping("/anime/search")
    public Result search(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "5") Integer pageSize,
                       String name){
        log.info("分页番名查询，参数：{},{},{}", page, pageSize, name);
        //调用service进行分页查询操作
        PageBean pageBean = animeService.search(page,pageSize,name);
        return Result.success(pageBean);

    }

    //解决跨域问题
    @CrossOrigin
    @GetMapping("anime/getAll")
    public Result getAll(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "300") Integer pageSize){
        PageBean pageBean = animeService.getAll(page,pageSize);
        return Result.success(pageBean);
    }
}