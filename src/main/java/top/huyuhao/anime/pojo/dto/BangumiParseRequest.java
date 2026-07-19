package top.huyuhao.anime.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Bangumi 解析请求
 */
@Data
@Schema(description = "Bangumi 解析请求体")
public class BangumiParseRequest {

    @Schema(description = "Bangumi 条目地址", example = "https://bangumi.tv/subject/265")
    private String bangumiUrl;
}
