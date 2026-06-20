package top.huyuhao.anime.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.huyuhao.anime.service.FileService;

@RestController
@RequestMapping("/file")
@Tag(name = "文件服务", description = "静态文件/图片访问")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/{*path}")
    @Operation(summary = "获取文件", description = "根据路径获取上传的文件（封面图片等）")
    public ResponseEntity<Resource> getFile(@Parameter(description = "文件相对路径") @PathVariable String path) {
        String fullPath = fileService.getFullPath(path);
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
