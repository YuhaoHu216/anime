package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.Tag;
import top.huyuhao.anime.service.AdminService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admin")
@io.swagger.v3.oas.annotations.tags.Tag(name = "管理员", description = "审核管理、标签管理")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/reviews")
    @Operation(summary = "获取待审核动漫列表")
    public Result getReviews(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                             @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
                             @Parameter(description = "动漫名称筛选") @RequestParam(required = false) String name) {
        return Result.success(adminService.getPendingReviews(page, pageSize, name));
    }

    @PostMapping("/review/{id}")
    @Operation(summary = "审核动漫", description = "对用户提交的动漫进行审核，通过或驳回。管理员身份从 JWT 获取。")
    public Result review(@Parameter(description = "动漫ID") @PathVariable Integer id,
                         @Parameter(description = "审核状态（approved/rejected）") @RequestParam String reviewStatus,
                         @Parameter(description = "审核备注") @RequestParam(required = false) String reviewComment) {
        Integer adminId = UserContext.getUserId();
        return adminService.reviewAnime(id, reviewStatus, reviewComment, adminId);
    }

    @GetMapping("/tags")
    @Operation(summary = "获取所有标签")
    public Result getTags() {
        List<Tag> tags = adminService.getAllTags();
        return Result.success(tags);
    }

    @PostMapping("/tags")
    @Operation(summary = "创建标签")
    public Result createTag(@Parameter(description = "标签名称") @RequestParam String name) {
        return adminService.createTag(name);
    }

    @DeleteMapping("/tags/{id}")
    @Operation(summary = "删除标签")
    public Result deleteTag(@Parameter(description = "标签ID") @PathVariable Integer id) {
        return adminService.deleteTag(id);
    }
}
