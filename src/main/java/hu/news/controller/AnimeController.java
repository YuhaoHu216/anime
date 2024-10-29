package hu.news.controller;

import hu.news.pojo.Anime;
import hu.news.pojo.AnimeWatched;
import hu.news.pojo.PageBean;
import hu.news.pojo.Result;
import hu.news.service.AnimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    //分页番名查询功能
    @GetMapping("/anime/search")
    public Result search(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "5") Integer pageSize,
                       String name,String state){
        log.info("分页番名查询，参数：{},{},{},{}", page, pageSize, name,state);
        //调用service进行分页查询操作
        PageBean pageBean = animeService.search(page,pageSize,name,state);
        return Result.success(pageBean);

    }

    //观看记录查询
    @GetMapping("/anime/watched/{year}")
    public Result getAllWatched(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "5") Integer pageSize,
                             String name,
                             @PathVariable Integer year){
        log.info("获取观看记录{},名:{}",year,name);
        PageBean pageBean = animeService.getAllWatched(page,pageSize,name,year);
        return Result.success(pageBean);
    }

    //补番目录新增
    @PostMapping("/anime/unwatched/add")
    public Result addAnime(@RequestBody Anime anime){
        log.info("新增番剧:{}",anime);
        animeService.addAnime(anime);
        return Result.success("新增成功");
    }

    //补番目录删除
    @GetMapping("/anime/unwatched/delete/{id}")
    public Result deleteAnime(@PathVariable Integer id){
        log.info("删除番剧,id:{}",id);
        animeService.deleteAnime(id);
        return Result.success("删除成功");
    }

    //补番目录修改
    @PostMapping("/anime/unwatched/update")
    public Result updateAnime(@RequestBody Anime anime){
        log.info("修改番剧,{}",anime);
        animeService.updateAnime(anime);
        return Result.success("修改成功");
    }

    //观番记录新增
    @PostMapping("/anime/watched/add")
    public Result addWatched(@RequestBody AnimeWatched animeWatched){
        log.info("新增记录:{}",animeWatched);
        animeService.addWatched(animeWatched);
        return Result.success("新增成功");
    }

    //观番记录修改(不要使用,还不完善)
    @PostMapping("/anime/watched/update")
    public Result updateWatched(@RequestBody AnimeWatched animeWatched){
        log.info("观番记录修改,{}",animeWatched);
        animeService.updateWatched(animeWatched);
        return Result.success("修改成功");
    }
}