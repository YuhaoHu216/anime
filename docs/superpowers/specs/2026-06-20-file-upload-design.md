# 文件上传重构设计

## 目标

将封面上传逻辑从 AnimeController 剥离到 FileController，实现"预分配 animeId → 上传封面 → 提交数据"的三步流程，上传返回完整可访问 URL。

## 流程

```
① POST /api/anime/prepare               →  预分配 animeId，返回 { animeId: 123 }
② POST /api/file/upload?animeId=123     →  上传封面到 D:\uploads\covers\123_{uuid}.ext
                                           →  返回 { url: "http://host/api/file/covers/123_xxx.png" }
③ POST /api/anime/add                    →  提交动漫数据（JSON body，含 coverUrl），更新占位记录
```

## 文件改动清单

### 1. AnimeController

| 操作 | 端点 | 说明 |
|------|------|------|
| ➕ 新增 | `POST /anime/prepare` | 预分配 animeId，返回 `Result.success(animeId)` |
| 🔧 修改 | `POST /anime/add` | 改为只接收 `@RequestBody AnimeAddDTO`（去掉 MultipartFile） |
| 🔧 修改 | `PUT /anime/{id}` | 改为只接收 `@RequestBody AnimeAddDTO`（去掉 MultipartFile） |
| 🔧 修改 | `POST /anime/submit` | 改为只接收 `@RequestBody AnimeAddDTO`（去掉 MultipartFile） |

### 2. FileController

| 操作 | 端点 | 说明 |
|------|------|------|
| ✅ 保留 | `GET /file/{*path}` | 静态文件访问（必须保留，否则 URL 无法展示图片） |
| ➕ 新增 | `POST /file/upload` | 接收 `MultipartFile` + `animeId` 参数，返回完整 URL |

### 3. FileService 接口

| 操作 | 方法 | 说明 |
|------|------|------|
| ❌ 删除 | `uploadCover(MultipartFile, Integer)` | 被 `upload` 替代 |
| ❌ 删除 | `getFullPath(String)` | 不再需要 |
| ➕ 新增 | `upload(MultipartFile file, Integer animeId)` | 保存到 `{uploadPath}/{coverDir}/{animeId}_{uuid}.ext`，返回完整 URL |

### 4. FileServiceImpl

- 实现 `upload` 方法
- 文件名格式：`{animeId}_{UUID前8位}.{原始扩展名}`
- 创建目录（如不存在）
- 返回：`http://{baseUrl}/api/file/{coverDir}/{fileName}`
- 需要注入 `server.port`、`server.servlet.context-path` 或配置一个 `app.base-url`

### 5. AnimeService 接口 + AnimeServiceImpl

| 操作 | 方法 | 说明 |
|------|------|------|
| ➕ 新增 | `prepareAnime()` | 插入一条 `review_status='preparing'` 的占位记录，返回自增 ID |
| 🔧 修改 | `addAnime(Anime, List<Integer>)` | 改为调用 `animeMapper.update()` 更新已有记录（而非 insert） |

### 6. 配置文件 application.yml

新增 `app.base-url` 配置项（可选，用于拼接完整 URL）：

```yaml
app:
  upload:
    path: D:/uploads          # 从 ./uploads 改为 D:/uploads
    cover-dir: covers
  base-url: http://localhost:8081/api   # 用于拼接文件访问 URL
```

## 配置说明

| 配置项 | 当前值 | 新值 | 说明 |
|--------|--------|------|------|
| `app.upload.path` | `./uploads` | `D:/uploads` | 用户指定上传目录 |
| `app.upload.cover-dir` | `covers` | `covers` | 不变 |
| `app.base-url` | 无 | `http://localhost:8081/api` | 新增，拼接完整 URL |

## 注意事项

1. `prepareAnime` 插入的记录若最终未使用（用户放弃上传），会成为僵尸数据。可接受，也可后续增加定时清理。
2. 上传接口需要限制文件大小和类型（复用现有 `spring.servlet.multipart` 配置）。
3. AnimeAddDTO 已有 `coverUrl` 字段，无需修改。
4. FileController 的 GET 接口必须保留，否则上传返回的 URL 无法访问图片。
