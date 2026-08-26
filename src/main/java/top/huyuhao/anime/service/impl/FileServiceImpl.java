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

    @Value("${app.upload.avatar-dir}")
    private String avatarDir;

    @Value("${app.upload.comment-dir}")
    private String commentDir;

    @Override
    public String upload(MultipartFile file, Integer animeId) {
        return saveFile(file, coverDir, animeId + "_");
    }

    @Override
    public String uploadAvatar(MultipartFile file, Integer userId) {
        return saveFile(file, avatarDir, userId + "_");
    }

    @Override
    public String uploadComment(MultipartFile file, Integer userId) {
        return saveFile(file, commentDir, userId + "_");
    }

    /**
     * 保存文件到指定目录，文件名 {prefix}{UUID}.ext
     * @return 相对路径，如 covers/123_abc.png（前端 getFileUrl 会拼成 /api/file/... 走同源协议，http/https 自适应）
     */
    private String saveFile(MultipartFile file, String dir, String namePrefix) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 获取原始扩展名
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        // 生成文件名：{prefix}{UUID}.ext
        String fileName = namePrefix + UUID.randomUUID().toString().substring(0, 8) + ext;

        try {
            Path targetDir = Paths.get(uploadPath, dir);
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(fileName);
            file.transferTo(targetPath.toFile());
            return dir + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
}
