# 封面图 URL http→相对路径 修复方案

## 背景

服务器新增 https 证书后，页面通过 https 访问，但数据库 `anime.cover_url` 里存的封面图 URL 是带 `http://` 的绝对地址，浏览器会拦截混合内容（mixed content），导致图片无法显示。

**根因**：后端 `FileServiceImpl.upload` 用配置 `app.base-url`（写死 `http://...`）拼接出绝对 URL 返回给前端，前端 `getFileUrl` 检测到 `http` 开头就原样使用。

**关键事实**：
- 前端 `src/api/file.js` 的 `getFileUrl` 已支持相对路径：`if (path.startsWith('http')) return path; return '/api/file/' + path`，即相对路径会自动拼成同源 URL（http 页面 → http，https 页面 → https）。
- 数据库建表脚本注释本就写着 `cover_url ... 封面图本地路径 covers/{animeId}_{UUID}.jpg`，即设计初衷就是存相对路径。
- 前端 `request.js` 的 `baseURL: '/api'`、vite 开发代理 `/api → 127.0.0.1:8081`、生产 nginx 反代 `/api`，均已走同源相对路径架构。

因此最优解：**让上传接口返回相对路径**，开发 http、生产 https 自动兼容，前端无需改动。

## 改动清单

### 1. `src/main/java/top/huyuhao/anime/service/impl/FileServiceImpl.java`
- 删除 `baseUrl` 字段及 `@Value("${app.base-url}")` 注解。
- `upload` 方法返回语句由 `baseUrl + "/file/" + coverDir + "/" + fileName` 改为 `coverDir + "/" + fileName`，即返回 `covers/xxx.jpg` 相对路径。

### 2. `src/main/java/top/huyuhao/anime/service/FileService.java`
- 更新接口注释 `@return` 说明：改为「封面相对路径，如 covers/123_abc.png」。

### 3. `src/main/resources/application-dev.yml`
- 删除 `app.base-url: http://localhost:8081/api`。

### 4. `src/main/resources/application-prod.yml`
- 删除 `app.base-url: ${APP_BASE_URL:http://8.156.66.24:8081/api}`。

> `app.base-url` 仅被 `FileServiceImpl` 使用，改造后无其他引用，按「不冗余」原则一并删除。

### 5. 历史数据迁移 SQL（在生产库手动执行一次）
```sql
UPDATE anime
SET cover_url = SUBSTRING_INDEX(cover_url, '/file/', -1)
WHERE cover_url LIKE '%/file/%';
```
效果：`http://8.156.66.24:8081/api/file/covers/123_ab.jpg` → `covers/123_ab.jpg`。

- `WHERE cover_url LIKE '%/file/%'` 只会命中本地上传的封面，不会误伤 Bangumi 外链（如 `https://lain.bgm.tv/...`）。
- 开发环境测试数据同样会被正确转成相对路径。

## 前端
- 无需改动。`getFileUrl` 对相对路径 `covers/xxx.jpg` 会拼成 `/api/file/covers/xxx.jpg`，走同源协议，http/https 自动匹配。

## 备注
- 用户头像 avatarUrl 本次不处理（暂不能上传头像），后续若开放头像上传再统一接入相对路径逻辑。
