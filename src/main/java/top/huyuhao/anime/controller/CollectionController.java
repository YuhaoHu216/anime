package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.pojo.Collection;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.service.CollectionService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/collection")
@Tag(name = "收藏夹管理", description = "用户收藏夹的创建、编辑和内容管理")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @GetMapping("/list")
    @Operation(summary = "获取当前用户的收藏夹列表", description = "用户身份从 JWT 获取")
    public Result list() {
        Integer userId = UserContext.getUserId();
        List<Collection> collections = collectionService.getUserCollections(userId);
        return Result.success(collections);
    }

    @PostMapping("/create")
    @Operation(summary = "创建收藏夹", description = "用户身份从 JWT 获取")
    public Result create(@RequestBody Collection collection) {
        try {
            // 从 JWT 设置当前用户 ID，防止越权创建
            collection.setUserId(UserContext.getUserId());
            Collection created = collectionService.createCollection(collection);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑收藏夹")
    public Result update(@Parameter(description = "收藏夹ID") @PathVariable Integer id,
                         @RequestBody Collection collection) {
        collection.setId(id);
        try {
            collectionService.updateCollection(collection);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除收藏夹")
    public Result delete(@Parameter(description = "收藏夹ID") @PathVariable Integer id) {
        try {
            collectionService.deleteCollection(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "获取收藏夹内容")
    public Result getItems(@Parameter(description = "收藏夹ID") @PathVariable Integer id,
                           @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                           @Parameter(description = "每页条数") @RequestParam(defaultValue = "50") Integer pageSize) {
        return Result.success(collectionService.getItems(id, page, pageSize));
    }

    @PostMapping("/{id}/add")
    @Operation(summary = "向收藏夹添加动漫")
    public Result addItem(@Parameter(description = "收藏夹ID") @PathVariable Integer id,
                          @Parameter(description = "动漫ID") @RequestParam Integer animeId) {
        try {
            collectionService.addItem(id, animeId);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/remove/{animeId}")
    @Operation(summary = "从收藏夹移除动漫")
    public Result removeItem(@Parameter(description = "收藏夹ID") @PathVariable Integer id,
                             @Parameter(description = "动漫ID") @PathVariable Integer animeId) {
        try {
            collectionService.removeItem(id, animeId);
            return Result.success("移除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/move/{animeId}")
    @Operation(summary = "移动动漫到其他收藏夹")
    public Result moveItem(@Parameter(description = "源收藏夹ID") @RequestParam Integer fromCollectionId,
                           @Parameter(description = "目标收藏夹ID") @RequestParam Integer toCollectionId,
                           @Parameter(description = "动漫ID") @PathVariable Integer animeId) {
        try {
            collectionService.moveItem(fromCollectionId, toCollectionId, animeId);
            return Result.success("移动成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
