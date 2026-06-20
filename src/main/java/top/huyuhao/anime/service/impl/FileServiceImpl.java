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

    @Override
    public String uploadCover(MultipartFile file, Integer animeId) {
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

            return coverDir + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFullPath(String relativePath) {
        return Paths.get(uploadPath, relativePath).toString();
    }
}
