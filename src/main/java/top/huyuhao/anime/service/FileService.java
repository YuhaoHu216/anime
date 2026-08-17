package top.huyuhao.anime.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件（通用）
     * @param file 文件
     * @param animeId 动漫ID（用于命名）
     * @return 封面相对路径，如 covers/123_abc.png（前端 getFileUrl 会拼成 /api/file/covers/123_abc.png）
     */
    String upload(MultipartFile file, Integer animeId);

    /**
     * 上传头像
     * @param file 图片文件
     * @param userId 用户ID（用于命名）
     * @return 头像相对路径，如 avatars/123_abc.png
     */
    String uploadAvatar(MultipartFile file, Integer userId);
}
