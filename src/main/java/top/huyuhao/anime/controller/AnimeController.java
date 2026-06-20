package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.dto.AnimeAddDTO;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.service.AnimeService;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/anime")
@Tag(name = "动漫管理", description = "动漫信息的增删改查、搜索和用户提交")
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    @PostMapping("/prepare")
    @Operation(summary = "预分配动漫ID", description = "新增动漫前预占一个ID，用于先上传封面再提交数据")
    public Result prepare() {
        Integer animeId = animeService.prepareAnime();
        log.info("预分配动漫ID: {}", animeId);
        return Result.success(animeId);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索动漫", description = "支持按名称、状态、标签ID分页搜索动漫")
    public Result search(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                         @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
                         @Parameter(description = "动漫名称（模糊搜索）") @RequestParam(required = false) String name,
                         @Parameter(description = "状态筛选") @RequestParam(required = false) String state,
                         @Parameter(description = "标签ID筛选") @RequestParam(required = false) Integer tagId) {
        log.info("搜索动漫: name={}, state={}, tagId={}", name, state, tagId);
        return Result.success(animeService.search(page, pageSize, name, state, tagId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取动漫详情")
    public Result getById(@Parameter(description = "动漫ID") @PathVariable Integer id) {
        return animeService.findById(id);
    }

    @PostMapping("/add")
    @Operation(summary = "添加动漫", description = "添加一部新动漫（封面需先通过 /file/upload 上传）")
    public Result addAnime(@Parameter(description = "动漫信息（JSON）") @RequestBody AnimeAddDTO dto) {
        log.info("添加动漫: {}", dto.getNameCn());
        Anime anime = dto.toAnime();
        return animeService.addAnime(anime, dto.getTagIds());
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改动漫", description = "根据ID修改动漫信息（封面需先通过 /file/upload 上传）")
    public Result updateAnime(@Parameter(description = "动漫ID") @PathVariable Integer id,
                              @Parameter(description = "动漫信息（JSON）") @RequestBody AnimeAddDTO dto) {
        log.info("修改动漫: id={}", id);
        Anime anime = dto.toAnime();
        anime.setId(id);
        return animeService.updateAnime(anime, dto.getTagIds());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除动漫", description = "根据ID删除动漫")
    public Result deleteAnime(@Parameter(description = "动漫ID") @PathVariable Integer id) {
        log.info("删除动漫: id={}", id);
        return animeService.deleteAnime(id);
    }

    @PostMapping("/submit")
    @Operation(summary = "用户提交动漫", description = "用户提交一部动漫，需管理员审核后入库。用户身份从 JWT 获取。")
    public Result submitAnime(@Parameter(description = "动漫信息（JSON）") @RequestBody AnimeAddDTO dto) {
        Integer userId = UserContext.getUserId();
        log.info("用户提交动漫: {}, userId={}", dto.getNameCn(), userId);
        Anime anime = dto.toAnime();
        return animeService.submitAnime(anime, dto.getTagIds(), userId);
    }
}
