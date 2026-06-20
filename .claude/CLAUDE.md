# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 构建与运行

```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 运行单个测试类
mvn test -Dtest=NewsApplicationTests

# 启动应用（开发环境）
mvn spring-boot:run

# 打包
mvn clean package -DskipTests
```

应用默认运行在 **8081** 端口。

## 技术栈

- Java 17 + Spring Boot 3.3.2
- MyBatis + PageHelper（分页查询）
- MySQL 数据库（数据库名 `anime`，用户 `root`，密码 `123456`）
- Lombok
- 阿里云 OSS SDK

## 架构概览

标准分层架构，包路径 `top.huyuhao.anime`：

| 层 | 目录 | 职责 |
|---|---|---|
| Controller | `controller/` | REST API 端点，`@CrossOrigin` 跨域 |
| Service | `service/` | 业务接口 + `service/impl/` 实现 |
| Mapper | `mapper/` | MyBatis 数据访问，注解+XML混合 |
| POJO | `pojo/` | 数据模型 |

### 核心数据模型

- **Anime** — 未看番剧（表 `unwatched`），字段：id, name, broadcastTime, bangumiScore, episode, state
- **AnimeWatched** — 已看记录（按年份分表 `watched{year}`），字段：watchTime, name, broadcastTime, bangumiScore, episode, year
- **User** — 用户（表 `user`），字段：username, password, phoneNumber

### 统一响应格式

`Result` 类封装所有 API 响应，`code=1` 为成功，`code=0` 为失败，格式 `{code, msg, data}`。分页结果用 `PageBean {total, rows}` 封装。

### 已看记录按年分表

观看记录使用动态表名——`AnimeWatched.year` 字段拼接到表名（例如 `watched2024`、`watched2025`）。Mapper 通过 `${table}` 拼接表名，ServiceImpl 负责拼好表名后传入。

### MyBatis 使用规范

- 简单 CRUD：直接在 Mapper 接口上使用注解（`@Select`、`@Insert`、`@Update`、`@Delete`）
- 复杂/动态查询（含 `<if>` 条件）：使用 XML Mapper 文件（如 `AnimeMapper.xml`）

## 重要注意事项

**当前正在进行重构**——代码从旧包 `hu.news` 迁移到 `top.huyuhao.anime`。请注意：

1. Java 源文件已经迁移完成（git status 显示 `RM` 状态）
2. MyBatis XML Mapper 文件 `src/main/resources/hu/news/mapper/AnimeMapper.xml` 已被修改（git status 显示 `M`），**尚未移动到新位置** `src/main/resources/top/huyuhao/anime/mapper/`。需要将此文件移动到与新包结构匹配的路径，否则 MyBatis 无法自动发现。
3. `application.yml` 中 `mybatis.configuration.map-underscore-to-camel-case: true` 已启用字段名自动映射，但请注意 `AnimeMapper.xml` 中手动映射了 `bangumi_score` 字段。