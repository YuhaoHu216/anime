package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Collection;
import top.huyuhao.anime.pojo.CollectionItem;
import top.huyuhao.anime.pojo.Result;

import java.util.List;

public interface CollectionService {

    // 收藏夹 CRUD
    Result createCollection(Collection collection);

    Result updateCollection(Collection collection);

    Result deleteCollection(Integer id);

    List<Collection> getUserCollections(Integer userId);

    // 收藏条目操作
    Result addItem(Integer collectionId, Integer animeId);

    Result removeItem(Integer collectionId, Integer animeId);

    Result moveItem(Integer fromCollectionId, Integer toCollectionId, Integer animeId);

    List<CollectionItem> getItems(Integer collectionId, Integer page, Integer pageSize);
}
