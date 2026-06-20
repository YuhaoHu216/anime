package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.WatchLog;
import top.huyuhao.anime.service.WatchLogService;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/watch")
@Tag(name = "追番记录", description = "用户追番日志的记录、查询和日历视图")
public class WatchLogController {

    @Autowired
    private WatchLogService watchLogService;

    @PostMapping("/log")
    @Operation(summary = "添加追番记录")
    public Result addLog(@RequestBody WatchLog watchLog) {
        try {
            watchLogService.addLog(watchLog);
            return Result.success("记录成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/log/{id}")
    @Operation(summary = "更新追番记录")
    public Result updateLog(@Parameter(description = "记录ID") @PathVariable Integer id,
                            @RequestBody WatchLog watchLog) {
        watchLog.setId(id);
        try {
            watchLogService.updateLog(watchLog);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/log/{id}")
    @Operation(summary = "删除追番记录")
    public Result deleteLog(@Parameter(description = "记录ID") @PathVariable Integer id) {
        try {
            watchLogService.deleteLog(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/logs")
    @Operation(summary = "分页查询追番记录", description = "支持按用户、动漫、日期范围筛选")
    public Result getLogs(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                          @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer pageSize,
                          @Parameter(description = "用户ID") @RequestParam Integer userId,
                          @Parameter(description = "动漫ID（可选）") @RequestParam(required = false) Integer animeId,
                          @Parameter(description = "开始日期（yyyy-MM-dd）") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                          @Parameter(description = "结束日期（yyyy-MM-dd）") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(watchLogService.getLogs(page, pageSize, userId, animeId, startDate, endDate));
    }

    @GetMapping("/logs/date")
    @Operation(summary = "按日期查询追番记录")
    public Result getLogsByDate(@Parameter(description = "用户ID") @RequestParam Integer userId,
                                 @Parameter(description = "日期（yyyy-MM-dd）") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<WatchLog> logs = watchLogService.getLogsByDate(userId, date);
        return Result.success(logs);
    }

    @GetMapping("/calendar")
    @Operation(summary = "获取追番日历", description = "获取指定月份中用户追番的日期列表")
    public Result getCalendar(@Parameter(description = "用户ID") @RequestParam Integer userId,
                               @Parameter(description = "年份") @RequestParam Integer year,
                               @Parameter(description = "月份") @RequestParam Integer month) {
        List<LocalDate> dates = watchLogService.getCalendar(userId, year, month);
        return Result.success(dates);
    }
}
