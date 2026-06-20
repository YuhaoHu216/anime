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
