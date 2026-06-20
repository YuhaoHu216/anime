package top.huyuhao.anime.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传封面图
     * @param file 图片文件
     * @param animeId 动漫ID（用于命名）
     * @return 相对路径 covers/{animeId}_{UUID}.ext
     */
    String uploadCover(MultipartFile file, Integer animeId);

    /**
     * 根据相对路径获取文件的完整本地路径
     */
    String getFullPath(String relativePath);
}
