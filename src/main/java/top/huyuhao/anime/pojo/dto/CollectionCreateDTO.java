package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.huyuhao.anime.pojo.Collection;

/**
 * 创建收藏夹请求 DTO，只暴露可填写的字段，userId 由 JWT 获取。
 */
@Data
@Schema(description = "创建收藏夹请求体")
public class CollectionCreateDTO {

    @Schema(description = "收藏夹名称", example = "我的追番列表")
    private String name;

    @Schema(description = "收藏夹描述", example = "2025年冬季新番")
    private String description;

    @Schema(description = "是否公开", example = "false")
    private Boolean isPublic;

    @Schema(description = "排序序号", example = "0")
    private Integer sortOrder;

    /**
     * 将 DTO 转换为 Collection 实体对象（不含 userId，由调用方设置）
     */
    public Collection toCollection() {
        Collection collection = new Collection();
        collection.setName(this.name);
        collection.setDescription(this.description);
        collection.setIsPublic(this.isPublic);
        collection.setSortOrder(this.sortOrder);
        return collection;
    }
}
