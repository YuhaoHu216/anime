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
