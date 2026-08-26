package top.huyuhao.anime.pojo.dto;

import lombok.Data;
import top.huyuhao.anime.pojo.Collection;
import top.huyuhao.anime.pojo.WatchLog;

import java.util.List;

/**
 * 分享主页聚合数据 —— 个人信息 + 公开收藏夹 + 追番统计/最近记录。
 * 各模块数据是否返回，由对应的隐私开关决定。
 */
@Data
public class ShareProfileDTO {
    /** 基本信息（profilePublic=false 时仅含 account） */
    private ShareUser user;

    /** 公开收藏夹列表（collectionPublic=true 时，且仅含 is_public 的收藏夹） */
    private List<Collection> publicCollections;

    /** 追番统计（watchPublic=true 时返回，含热力图） */
    private WatchStatsDTO stats;

    /** 最近追番记录（watchPublic=true 时返回） */
    private List<WatchLog> recentLogs;

    /** 有追番记录的年份列表（降序，watchPublic=true 时返回），供热力图年份下拉框使用 */
    private List<Integer> availableYears;

    /** 三个隐私开关，供前端判断各模块是否渲染 */
    private PrivacyUpdateDTO privacy;
}
