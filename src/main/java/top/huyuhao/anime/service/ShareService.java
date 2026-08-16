package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.CollectionItem;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.dto.ShareProfileDTO;

import java.time.LocalDate;

/**
 * 页面分享服务 —— 供未登录用户访问他人公开信息。
 */
public interface ShareService {

    /**
     * 获取指定账号的公开主页聚合数据（个人信息 + 公开收藏夹 + 追番统计/最近记录）
     */
    ShareProfileDTO getShareProfile(String account, LocalDate startDate, LocalDate endDate);

    /**
     * 获取公开收藏夹的动漫条目
     */
    PageBean<CollectionItem> getPublicCollectionItems(Integer collectionId, Integer page, Integer pageSize);
}
