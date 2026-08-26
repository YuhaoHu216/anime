package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.pojo.Comment;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.dto.CommentAddDTO;
import top.huyuhao.anime.pojo.dto.CommentLikeDTO;
import top.huyuhao.anime.service.CommentService;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/comment")
@Tag(name = "评论", description = "动漫评论的发表、查询、点赞与删除")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    @Operation(summary = "分页查询顶层评论", description = "公开可读；登录时返回当前用户点赞状态 myType，每条附最近3条子评论")
    public Result list(@Parameter(description = "动漫ID") @RequestParam Integer animeId,
                       @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                       @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageBean<Comment> data = commentService.list(animeId, page, pageSize, UserContext.getUserId());
        return Result.success(data);
    }

    @GetMapping("/children")
    @Operation(summary = "展开某顶层评论的全部子评论", description = "公开可读；登录时返回当前用户点赞状态 myType")
    public Result children(@Parameter(description = "顶层评论ID") @RequestParam Integer rootId) {
        List<Comment> data = commentService.children(rootId, UserContext.getUserId());
        return Result.success(data);
    }

    @PostMapping
    @Operation(summary = "发表评论", description = "顶层评论 parentId 为空；子评论 parentId 指向顶层评论，可带 replyToUserId 标识回复对象")
    public Result add(@RequestBody CommentAddDTO dto) {
        Comment comment = commentService.add(dto, UserContext.getUserId());
        return Result.success("评论成功", comment);
    }

    @PostMapping("/like")
    @Operation(summary = "点赞/点踩", description = "type=1赞/-1踩；再次点同类取消，点异类翻转")
    public Result like(@RequestBody CommentLikeDTO dto) {
        Map<String, Object> data = commentService.like(dto.getCommentId(), dto.getType(), UserContext.getUserId());
        return Result.success(data);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "评论作者本人或管理员可删")
    public Result delete(@Parameter(description = "评论ID") @PathVariable Integer id) {
        commentService.delete(id, UserContext.getUserId());
        return Result.success("删除成功");
    }
}
