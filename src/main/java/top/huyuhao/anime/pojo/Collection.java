package top.huyuhao.anime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Collection {
    private Integer id;
    private Integer userId;
    private String name;
    private String description;
    private Boolean isDefault;
    private Integer sortOrder;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 非数据库字段
    private Integer itemCount;
}
