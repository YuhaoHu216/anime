package top.huyuhao.anime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.huyuhao.anime.mapper.CollectionItemMapper;
import top.huyuhao.anime.mapper.CollectionMapper;
import top.huyuhao.anime.pojo.Collection;
import top.huyuhao.anime.pojo.CollectionItem;
import top.huyuhao.anime.service.CollectionService;

import java.util.List;

@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionItemMapper collectionItemMapper;

    @Override
    public Collection createCollection(Collection collection) {
        collection.setIsDefault(false);
        collectionMapper.insert(collection);
        return collection;
    }

    @Override
    public void updateCollection(Collection collection) {
        collectionMapper.update(collection);
    }

    @Override
    @Transactional
    public void deleteCollection(Integer id) {
        Collection c = collectionMapper.findById(id);
        if (c != null && c.getIsDefault()) {
            throw new RuntimeException("默认收藏夹不可删除");
        }
        collectionMapper.delete(id);
    }

    @Override
    public List<Collection> getUserCollections(Integer userId) {
        List<Collection> collections = collectionMapper.findByUserId(userId);
        for (Collection c : collections) {
            c.setItemCount(collectionItemMapper.countByCollectionId(c.getId()));
        }
        return collections;
    }

    @Override
    public void addItem(Integer collectionId, Integer animeId) {
        CollectionItem existing = collectionItemMapper.findByCollectionAndAnime(collectionId, animeId);
        if (existing != null) {
            throw new RuntimeException("该动漫已在此收藏夹中");
        }
        CollectionItem item = new CollectionItem();
        item.setCollectionId(collectionId);
        item.setAnimeId(animeId);
        collectionItemMapper.insert(item);
    }

    @Override
    public void removeItem(Integer collectionId, Integer animeId) {
        collectionItemMapper.deleteByCollectionAndAnime(collectionId, animeId);
    }

    @Override
    public void moveItem(Integer fromCollectionId, Integer toCollectionId, Integer animeId) {
        // 先检查目标收藏夹是否已有
        CollectionItem existing = collectionItemMapper.findByCollectionAndAnime(toCollectionId, animeId);
        if (existing != null) {
            // 目标已有，只需从源删除
            collectionItemMapper.deleteByCollectionAndAnime(fromCollectionId, animeId);
        } else {
            collectionItemMapper.moveAnime(fromCollectionId, toCollectionId, animeId);
        }
    }

    @Override
    public List<CollectionItem> getItems(Integer collectionId, Integer page, Integer pageSize) {
        // 简单不分页返回（后续可加 PageHelper）
        return collectionItemMapper.findByCollectionId(collectionId);
    }
}
