package top.huyuhao.anime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.huyuhao.anime.mapper.CollectionMapper;
import top.huyuhao.anime.mapper.UserMapper;
import top.huyuhao.anime.pojo.Collection;
import top.huyuhao.anime.pojo.CollectionItem;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.User;
import top.huyuhao.anime.pojo.WatchLog;
import top.huyuhao.anime.pojo.dto.PrivacyUpdateDTO;
import top.huyuhao.anime.pojo.dto.ShareProfileDTO;
import top.huyuhao.anime.pojo.dto.ShareUser;
import top.huyuhao.anime.service.CollectionService;
import top.huyuhao.anime.service.ShareService;
import top.huyuhao.anime.service.WatchLogService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private WatchLogService watchLogService;

    @Override
    public ShareProfileDTO getShareProfile(String account, LocalDate startDate, LocalDate endDate) {
        User user = userMapper.findByAccount(account);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        ShareProfileDTO dto = new ShareProfileDTO();

        // 个人信息：仅 profilePublic 时暴露 username/avatar/createdAt，始终只回传 account
        ShareUser shareUser = new ShareUser();
        shareUser.setAccount(user.getAccount());
        if (Boolean.TRUE.equals(user.getProfilePublic())) {
            shareUser.setUsername(user.getUsername());
            shareUser.setAvatarUrl(user.getAvatarUrl());
            shareUser.setCreatedAt(user.getCreatedAt());
        }
        dto.setUser(shareUser);

        // 三个隐私开关回传，供前端判断各模块渲染
        PrivacyUpdateDTO privacy = new PrivacyUpdateDTO();
        privacy.setProfilePublic(user.getProfilePublic());
        privacy.setCollectionPublic(user.getCollectionPublic());
        privacy.setWatchPublic(user.getWatchPublic());
        dto.setPrivacy(privacy);

        Integer userId = user.getId();

        // 收藏夹：collectionPublic 时仅返回 is_public 的收藏夹
        if (Boolean.TRUE.equals(user.getCollectionPublic())) {
            List<Collection> publicCollections = collectionService.getUserCollections(userId).stream()
                    .filter(c -> Boolean.TRUE.equals(c.getIsPublic()))
                    .collect(Collectors.toList());
            dto.setPublicCollections(publicCollections);
        }

        // 追番：watchPublic 时返回统计 + 最近记录
        if (Boolean.TRUE.equals(user.getWatchPublic())) {
            if (startDate == null) startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
            if (endDate == null) endDate = LocalDate.now();
            dto.setStats(watchLogService.getStats(userId, startDate, endDate));
            dto.setRecentLogs(watchLogService.getRecentLogs(userId, 5));
            dto.setAvailableYears(watchLogService.getWatchYears(userId));
        }

        return dto;
    }

    @Override
    public PageBean<CollectionItem> getPublicCollectionItems(Integer collectionId, Integer page, Integer pageSize) {
        Collection collection = collectionMapper.findById(collectionId);
        if (collection == null || !Boolean.TRUE.equals(collection.getIsPublic())) {
            throw new RuntimeException("该收藏夹未公开");
        }
        return collectionService.getItems(collectionId, page, pageSize);
    }

    @Override
    public List<WatchLog> getShareLogsByDate(String account, LocalDate date) {
        User user = userMapper.findByAccount(account);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!Boolean.TRUE.equals(user.getWatchPublic())) {
            throw new RuntimeException("该用户未公开追番记录");
        }
        return watchLogService.getLogsByDate(user.getId(), date);
    }
}
