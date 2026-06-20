package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.dto.AnimeAddDTO;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.service.AnimeService;
import top.huyuhao.anime.service.FileService;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/anime")
@Tag(name = "动漫管理", description = "动漫信息的增删改查、搜索和用户提交")
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    @Autowired
    private FileService fileService;

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
        Anime anime = animeService.findById(id);
        if (anime == null) {
            return Result.error("动漫不存在");
        }
        return Result.success(anime);
    }

    @PostMapping("/add")
    @Operation(summary = "添加动漫", description = "添加一部新动漫，可同时上传封面图片和关联标签")
    public Result addAnime(@Parameter(description = "动漫信息（JSON）") @RequestBody AnimeAddDTO dto) {
        log.info("添加动漫: {}", dto.getNameCn());
            Anime anime = dto.toAnime();
            return animeService.addAnime(anime, dto.getTagIds());
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改动漫", description = "根据ID修改动漫信息，可同时更新封面图片和标签")
    public Result updateAnime(@Parameter(description = "动漫ID") @PathVariable Integer id,
                              @Parameter(description = "动漫信息（JSON）") @RequestPart("anime") AnimeAddDTO dto,
                              @RequestPart(value = "cover", required = false) MultipartFile cover) {
        log.info("修改动漫: id={}", id);
        try {
            Anime anime = dto.toAnime();
            anime.setId(id);
            if (cover != null && !cover.isEmpty()) {
                String coverPath = fileService.uploadCover(cover, id);
                anime.setCoverUrl(coverPath);
            }
            animeService.updateAnime(anime, dto.getTagIds());
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除动漫", description = "根据ID删除动漫")
    public Result deleteAnime(@Parameter(description = "动漫ID") @PathVariable Integer id) {
        log.info("删除动漫: id={}", id);
        try {
            animeService.deleteAnime(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/submit")
    @Operation(summary = "用户提交动漫", description = "用户提交一部动漫，需管理员审核后入库。用户身份从 JWT 获取。")
    public Result submitAnime(@Parameter(description = "动漫信息（JSON）") @RequestPart("anime") AnimeAddDTO dto,
                              @RequestPart(value = "cover", required = false) MultipartFile cover) {
        Integer userId = UserContext.getUserId();
        log.info("用户提交动漫: {}, userId={}", dto.getNameCn(), userId);
        try {
            Anime anime = dto.toAnime();
            animeService.submitAnime(anime, dto.getTagIds(), userId);
            if (cover != null && !cover.isEmpty()) {
                String coverPath = fileService.uploadCover(cover, anime.getId());
                anime.setCoverUrl(coverPath);
                animeService.updateAnime(anime, null);
            }
            return Result.success("提交成功，等待管理员审核");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
