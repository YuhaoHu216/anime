# 文件上传重构 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将封面上传逻辑从 AnimeController 剥离到 FileController，实现"预分配 animeId → 上传封面 → 提交数据"三步流程，上传接口返回完整可访问 URL。

**Architecture:** 前端先调 `/anime/prepare` 获取预分配 ID，再用 ID 调 `/file/upload` 上传封面得到完整 URL，最后调 `/anime/add` 提交完整数据。FileController 统一管理文件上传+访问，AnimeController 只负责业务数据。

**Tech Stack:** Spring Boot + MyBatis + MultipartFile

---

### Task 1: 修改 FileService 接口

**Files:**
- Modify: `src/main/java/top/huyuhao/anime/service/FileService.java`

- [ ] **Step 1: 重写 FileService 接口**

删除 `uploadCover` 和 `getFullPath`，新增 `upload` 方法：

```java
package top.huyuhao.anime.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件（通用）
     * @param file 文件
     * @param animeId 动漫ID（用于命名）
     * @return 完整可访问URL，如 http://localhost:8081/api/file/covers/123_abc.png
     */
    String upload(MultipartFile file, Integer animeId);
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/top/huyuhao/anime/service/FileService.java
git commit -m "refactor: 重写 FileService 接口，新增 upload 方法"
```

---

### Task 2: 修改 FileServiceImpl 实现

**Files:**
- Modify: `src/main/java/top/huyuhao/anime/service/impl/FileServiceImpl.java`

- [ ] **Step 1: 重写 FileServiceImpl**

```java
package top.huyuhao.anime.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.huyuhao.anime.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${app.upload.path}")
    private String uploadPath;

    @Value("${app.upload.cover-dir}")
    private String coverDir;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public String upload(MultipartFile file, Integer animeId) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 获取原始扩展名
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        // 生成文件名：{animeId}_{UUID}.ext
        String fileName = animeId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

        try {
            Path dir = Paths.get(uploadPath, coverDir);
            Files.createDirectories(dir);
            Path targetPath = dir.resolve(fileName);
            file.transferTo(targetPath.toFile());

            // 返回完整可访问 URL
            return baseUrl + "/file/" + coverDir + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/top/huyuhao/anime/service/impl/FileServiceImpl.java
git commit -m "refactor: 重写 FileServiceImpl，upload 返回完整 URL"
```

---

### Task 3: 修改 FileController（新增上传接口）

**Files:**
- Modify: `src/main/java/top/huyuhao/anime/controller/FileController.java`

- [ ] **Step 1: 新增 POST /file/upload 接口，保留 GET 文件访问**

FileController 直接注入 `@Value("${app.upload.path}")` 替代原来调用 `fileService.getFullPath()`：

```java
package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.service.FileService;

import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
@Tag(name = "文件服务", description = "文件上传与访问")
public class FileController {

    @Autowired
    private FileService fileService;

    @Value("${app.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件到指定目录，返回完整可访问URL")
    public Result upload(@Parameter(description = "文件") @RequestParam("file") MultipartFile file,
                         @Parameter(description = "动漫ID（用于文件命名）") @RequestParam Integer animeId) {
        String url = fileService.upload(file, animeId);
        return Result.success(url);
    }

    @GetMapping("/{*path}")
    @Operation(summary = "获取文件", description = "根据路径获取上传的文件")
    public ResponseEntity<Resource> getFile(@Parameter(description = "文件相对路径") @PathVariable String path) {
        String fullPath = Paths.get(uploadPath, path).toString();
        Resource resource = new FileSystemResource(fullPath);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/top/huyuhao/anime/controller/FileController.java
git commit -m "feat: FileController 新增 POST /file/upload 接口"
```

---

### Task 4: 修改 AnimeAddDTO（新增 id 字段）

**Files:**
- Modify: `src/main/java/top/huyuhao/anime/pojo/dto/AnimeAddDTO.java`

- [ ] **Step 1: 新增 id 字段，toAnime() 中设置 id**

`addAnime` 改为 `update` 后，DTO 需要携带预分配的 animeId：

```java
package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.huyuhao.anime.pojo.Anime;

import java.util.List;

/**
 * 添加/修改动漫的请求 DTO，封装 anime 字段和 tagIds，避免 Controller 参数过多。
 */
@Data
@Schema(description = "动漫添加/修改请求体")
public class AnimeAddDTO {

    @Schema(description = "动漫ID（新增时由 /anime/prepare 预分配；修改时由路径参数覆盖）", example = "123")
    private Integer id;

    @Schema(description = "中文名", example = "进击的巨人")
    private String nameCn;

    @Schema(description = "日文名", example = "進撃の巨人")
    private String nameJp;

    @Schema(description = "播出时间", example = "2013-04")
    private String broadcastTime;

    @Schema(description = "Bangumi 评分", example = "8.5")
    private String bangumiScore;

    @Schema(description = "总集数", example = "25")
    private Integer episode;

    @Schema(description = "封面图片 URL（一般由文件上传后回填）")
    private String coverUrl;

    @Schema(description = "动漫简介")
    private String description;

    @Schema(description = "关联标签 ID 列表", example = "[1, 2, 3]")
    private List<Integer> tagIds;

    /**
     * 将 DTO 转换为 Anime 实体对象
     */
    public Anime toAnime() {
        Anime anime = new Anime();
        anime.setId(this.id);
        anime.setNameCn(this.nameCn);
        anime.setNameJp(this.nameJp);
        anime.setBroadcastTime(this.broadcastTime);
        anime.setBangumiScore(this.bangumiScore);
        anime.setEpisode(this.episode);
        anime.setCoverUrl(this.coverUrl);
        anime.setDescription(this.description);
        return anime;
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/top/huyuhao/anime/pojo/dto/AnimeAddDTO.java
git commit -m "feat: AnimeAddDTO 新增 id 字段支持预分配ID"
```

---

### Task 5: 修改 AnimeService 接口

**Files:**
- Modify: `src/main/java/top/huyuhao/anime/service/AnimeService.java`

- [ ] **Step 1: 新增 prepareAnime 方法签名**

```java
package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;

import java.util.List;

public interface AnimeService {

    PageBean<Anime> search(Integer page, Integer pageSize, String name, String state, Integer tagId);

    Result findById(Integer id);

    /**
     * 预分配 animeId：插入占位记录并返回自增ID
     */
    Integer prepareAnime();

    Result addAnime(Anime anime, List<Integer> tagIds);

    Result updateAnime(Anime anime, List<Integer> tagIds);

    Result deleteAnime(Integer id);

    Result submitAnime(Anime anime, List<Integer> tagIds, Integer userId);
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/top/huyuhao/anime/service/AnimeService.java
git commit -m "feat: AnimeService 新增 prepareAnime 方法"
```

---

### Task 6: 修改 AnimeMapper

**Files:**
- Modify: `src/main/java/top/huyuhao/anime/mapper/AnimeMapper.java`

- [ ] **Step 1: 新增 prepareInsert 方法**

```java
@Insert("insert into anime(review_status) values ('preparing')")
@Options(useGeneratedKeys = true, keyProperty = "id")
void prepareInsert(Anime anime);
```

完整代码（在 AnimeMapper 接口中，加到 insert 方法后面即可）：

```java
package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.Anime;

import java.util.List;

@Mapper
public interface AnimeMapper {

    // 搜索已审核通过的动漫（分页由 PageHelper 处理）
    List<Anime> search(@Param("name") String name,
                       @Param("state") String state,
                       @Param("tagId") Integer tagId);

    @Select("select * from anime where id = #{id}")
    @Results({
            @Result(property = "nameCn", column = "name_cn"),
            @Result(property = "nameJp", column = "name_jp"),
            @Result(property = "broadcastTime", column = "broadcast_time"),
            @Result(property = "bangumiScore", column = "bangumi_score"),
            @Result(property = "coverUrl", column = "cover_url"),
            @Result(property = "reviewStatus", column = "review_status"),
            @Result(property = "submittedBy", column = "submitted_by"),
            @Result(property = "reviewedBy", column = "reviewed_by"),
            @Result(property = "reviewComment", column = "review_comment"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Anime findById(Integer id);

    @Insert("insert into anime(name_cn, name_jp, broadcast_time, bangumi_score, episode, " +
            "cover_url, description, review_status, submitted_by) " +
            "values (#{nameCn}, #{nameJp}, #{broadcastTime}, #{bangumiScore}, #{episode}, " +
            "#{coverUrl}, #{description}, #{reviewStatus}, #{submittedBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Anime anime);

    @Insert("insert into anime(review_status) values ('preparing')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void prepareInsert(Anime anime);

    @Update("update anime set name_cn = #{nameCn}, name_jp = #{nameJp}, " +
            "broadcast_time = #{broadcastTime}, bangumi_score = #{bangumiScore}, " +
            "episode = #{episode}, cover_url = #{coverUrl}, " +
            "description = #{description}, review_status = #{reviewStatus} where id = #{id}")
    void update(Anime anime);

    @Delete("delete from anime where id = #{id}")
    void delete(Integer id);

    // 审核相关
    @Update("update anime set review_status = #{reviewStatus}, reviewed_by = #{reviewedBy}, " +
            "review_comment = #{reviewComment} where id = #{id}")
    void review(@Param("id") Integer id,
                @Param("reviewStatus") String reviewStatus,
                @Param("reviewedBy") Integer reviewedBy,
                @Param("reviewComment") String reviewComment);

    // 待审核列表（分页由 PageHelper 处理）
    List<Anime> searchPending(@Param("name") String name);
}
```

**注意：** update 方法中移除了 `state = #{state}`（anime 表中没有 state 列，这是一个已有的 bug，顺带修复）。

- [ ] **Step 2: 提交**

```bash
git add src/main/java/top/huyuhao/anime/mapper/AnimeMapper.java
git commit -m "feat: AnimeMapper 新增 prepareInsert，修复 update SQL"
```

---

### Task 7: 修改 AnimeServiceImpl

**Files:**
- Modify: `src/main/java/top/huyuhao/anime/service/impl/AnimeServiceImpl.java`

- [ ] **Step 1: 实现 prepareAnime，修改 addAnime**

`addAnime` 从 `insert` 改为 `update`（因为 animeId 已由 prepare 预分配）。`submitAnime` 也改为 update。

```java
package top.huyuhao.anime.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.huyuhao.anime.mapper.AnimeMapper;
import top.huyuhao.anime.mapper.TagMapper;
import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.Tag;
import top.huyuhao.anime.service.AnimeService;

import java.util.List;

@Service
public class AnimeServiceImpl implements AnimeService {

    @Autowired
    private AnimeMapper animeMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public PageBean<Anime> search(Integer page, Integer pageSize, String name, String state, Integer tagId) {
        PageHelper.startPage(page, pageSize);
        List<Anime> animeList = animeMapper.search(name, state, tagId);
        // 为每个动漫加载标签
        for (Anime anime : animeList) {
            anime.setTags(tagMapper.findByAnimeId(anime.getId()));
        }
        Page<Anime> p = (Page<Anime>) animeList;
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    public Result findById(Integer id) {
        Anime anime = animeMapper.findById(id);
        if (anime == null) {
            throw new RuntimeException("动漫不存在");
        }
        anime.setTags(tagMapper.findByAnimeId(id));
        return Result.success(anime);
    }

    @Override
    @Transactional
    public Integer prepareAnime() {
        Anime anime = new Anime();
        animeMapper.prepareInsert(anime);
        return anime.getId();
    }

    @Override
    @Transactional
    public Result addAnime(Anime anime, List<Integer> tagIds) {
        anime.setReviewStatus("approved");
        animeMapper.update(anime);
        // 关联标签
        if (tagIds != null) {
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result updateAnime(Anime anime, List<Integer> tagIds) {
        animeMapper.update(anime);
        if (tagIds != null) {
            tagMapper.unlinkAllTags(anime.getId());
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success("更新成功");
    }

    @Override
    @Transactional
    public Result deleteAnime(Integer id) {
        tagMapper.unlinkAllTags(id);
        animeMapper.delete(id);
        return Result.success("删除成功");
    }

    @Override
    @Transactional
    public Result submitAnime(Anime anime, List<Integer> tagIds, Integer userId) {
        anime.setReviewStatus("pending");
        anime.setSubmittedBy(userId);
        animeMapper.update(anime);
        if (tagIds != null) {
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success("提交成功，等待管理员审核");
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/top/huyuhao/anime/service/impl/AnimeServiceImpl.java
git commit -m "refactor: AnimeServiceImpl 实现 prepareAnime，addAnime/submitAnime 改为 update"
```

---

### Task 8: 修改 AnimeController

**Files:**
- Modify: `src/main/java/top/huyuhao/anime/controller/AnimeController.java`

- [ ] **Step 1: 新增 prepare 接口，简化 add/update/submit**

去掉所有 MultipartFile 和 FileService 依赖：

```java
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
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/top/huyuhao/anime/controller/AnimeController.java
git commit -m "refactor: AnimeController 新增 prepare 接口，移除文件上传逻辑"
```

---

### Task 9: 修改配置文件

**Files:**
- Modify: `src/main/resources/application.yml`

- [ ] **Step 1: 更新 application.yml 上传配置**

```yaml
# 应用自定义配置
app:
  upload:
    path: D:/uploads
    cover-dir: covers
  base-url: http://localhost:8081/api
```

完整文件末尾部分：

```yaml
# 应用自定义配置
app:
  upload:
    path: D:/uploads
    cover-dir: covers
  base-url: http://localhost:8081/api
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/application.yml
git commit -m "config: 更新上传路径配置，新增 base-url"
```

---

### Task 10: 编译验证

**Files:** 无

- [ ] **Step 1: 编译项目**

```bash
mvn compile -q
```

预期：BUILD SUCCESS

- [ ] **Step 2: 提交（如有遗漏修改）**

```bash
git add -A && git diff --cached --stat
git commit -m "chore: 编译修复"  # 仅当有遗漏修改时
```
