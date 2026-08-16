package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.dto.AnimeAddDTO;
import top.huyuhao.anime.pojo.dto.AnimeUpdateDTO;
import top.huyuhao.anime.pojo.dto.BangumiInfo;
import top.huyuhao.anime.pojo.dto.BangumiParseRequest;
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
    public Result prepare() {
        Integer animeId = animeService.prepareAnime();
        log.info("预分配动漫ID: {}", animeId);
        return Result.success(animeId);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索动漫", description = "支持按名称、状态、标签ID分页搜索动漫")
    public Result search(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                         @Parameter(description = "每页条数") @RequestParam(defaultValue = "24") Integer pageSize,
                         @Parameter(description = "动漫名称（模糊搜索）") @RequestParam(required = false) String name,
                         @Parameter(description = "标签ID筛选") @RequestParam(required = false) Integer tagId) {
        log.info("搜索动漫: name={}, state={}, tagId={}", name, tagId);
        return Result.success(animeService.search(page, pageSize, name, tagId));
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "根据ID获取动漫详情")
    public Result getById(@Parameter(description = "动漫ID") @PathVariable Integer id) {
        return animeService.findById(id);
    }

    @PostMapping("/add")
    @Operation(summary = "添加动漫", description = "添加一部新动漫（封面需先通过 /file/upload 上传）")
    public Result addAnime(@Parameter(description = "动漫信息（JSON）") @RequestBody AnimeAddDTO dto) {
        log.info("添加动漫: {}", dto.getNameCn());
        Anime anime = dto.toAnime();
        return animeService.addAnime(anime, dto.getTagIds(), dto.getEpisodes());
    }

    @PutMapping("/update")
    @Operation(summary = "修改动漫", description = "根据ID修改动漫信息（封面需先通过 /file/upload 上传）")
    public Result updateAnime(@Parameter(description = "动漫信息（JSON）") @RequestBody AnimeUpdateDTO dto) {
        log.info("修改动漫: id={}", dto.getId());
        Anime anime = dto.toAnime();
        return animeService.updateAnime(anime, dto.getTagIds(), dto.getEpisodes());
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
        return animeService.submitAnime(anime, dto.getTagIds(), userId, dto.getEpisodes());
    }

    @GetMapping("/my-submissions")
    @Operation(summary = "获取当前用户的提交记录", description = "查询当前登录用户的所有提交记录，支持按审核状态筛选")
    public Result getMySubmissions(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                                   @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
                                   @Parameter(description = "审核状态筛选（pending/approved/rejected）") @RequestParam(required = false) String reviewStatus) {
        Integer userId = UserContext.getUserId();
        log.info("查询用户提交记录: userId={}, reviewStatus={}", userId, reviewStatus);
        return Result.success(animeService.getMySubmissions(page, pageSize, reviewStatus, userId));
    }

    @PostMapping("/parse-bangumi")
    public Result parseBangumi(@RequestBody BangumiParseRequest request) {
        log.info("解析 Bangumi 地址: {}", request.getBangumiUrl());
        BangumiInfo info = animeService.parseBangumiInfo(request.getBangumiUrl());
        return Result.success(info);
    }

    @GetMapping("/proxy-image")
    public ResponseEntity<Resource> proxyImage(@RequestParam String url) {
        Resource resource = animeService.proxyImage(url);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(resource);
    }
}
