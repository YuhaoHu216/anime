package top.huyuhao.anime.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 追番进度 —— 某用户某动漫的观看进度
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchProgressDTO {

    /** 已看到的数字正片集数（最大结束集，无记录返回 0） */
    private Integer maxEpEnd;

    /** 已看的所有集号（含 SP/OVA/OAD 番外，去重） */
    private List<String> watchedEpNos;

    /** 各集号的观看日期列表（yyyy-MM-dd，按日期升序、同一天去重） */
    private Map<String, List<String>> watchedEpDates;
}
