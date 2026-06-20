package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Collection;
import top.huyuhao.anime.pojo.CollectionItem;

import java.util.List;

public interface CollectionService {

    // 收藏夹 CRUD
    Collection createCollection(Collection collection);

    void updateCollection(Collection collection);

    void deleteCollection(Integer id);

    List<Collection> getUserCollections(Integer userId);

    // 收藏条目操作
    void addItem(Integer collectionId, Integer animeId);

    void removeItem(Integer collectionId, Integer animeId);

    void moveItem(Integer fromCollectionId, Integer toCollectionId, Integer animeId);

    List<CollectionItem> getItems(Integer collectionId, Integer page, Integer pageSize);
}
