package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.huyuhao.anime.pojo.Collection;

/**
 * 更新收藏夹请求 DTO，只暴露可修改的字段，id 由路径参数提供。
 */
@Data
@Schema(description = "更新收藏夹请求体")
public class CollectionUpdateDTO {

    @Schema(description = "收藏夹id", example = "1")
    private Integer id;

    @Schema(description = "收藏夹名称", example = "我的追番列表")
    private String name;

    @Schema(description = "收藏夹描述", example = "2025年冬季新番更新描述")
    private String description;

    @Schema(description = "是否公开", example = "true")
    private Boolean isPublic;

    @Schema(description = "排序序号", example = "1")
    private Integer sortOrder;

    /**
     * 将 DTO 转换为 Collection 实体对象
     */
    public Collection toCollection() {
        Collection collection = new Collection();
        collection.setId(this.id);
        collection.setName(this.name);
        collection.setDescription(this.description);
        collection.setIsPublic(this.isPublic);
        collection.setSortOrder(this.sortOrder);
        return collection;
    }
}
