package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.service.ShareService;

import java.time.LocalDate;

@CrossOrigin
@RestController
@RequestMapping("/share")
@Tag(name = "页面分享", description = "未登录用户访问他人公开主页、收藏夹、追番信息")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @GetMapping("/{account}")
    @Operation(summary = "获取公开主页", description = "按账号获取用户的公开信息（个人信息/公开收藏夹/追番统计），无需登录")
    public Result getShareProfile(@Parameter(description = "账号") @PathVariable String account,
                                  @Parameter(description = "追番热力图起始日期（yyyy-MM-dd），默认当年1月1日") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                  @Parameter(description = "追番热力图结束日期（yyyy-MM-dd），默认今天") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(shareService.getShareProfile(account, startDate, endDate));
    }

    @GetMapping("/collections/{collectionId}/items")
    @Operation(summary = "获取公开收藏夹内容", description = "获取公开收藏夹中的动漫条目，无需登录")
    public Result getPublicCollectionItems(@Parameter(description = "收藏夹ID") @PathVariable Integer collectionId,
                                           @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                                           @Parameter(description = "每页条数") @RequestParam(defaultValue = "50") Integer pageSize) {
        return Result.success(shareService.getPublicCollectionItems(collectionId, page, pageSize));
    }
}
