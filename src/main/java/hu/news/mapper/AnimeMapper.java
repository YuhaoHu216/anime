package hu.news.mapper;

import hu.news.pojo.Anime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnimeMapper {
//    @Select("select * from 补番目录 where name like concat('%',#{name},'%')")
    List<Anime> search(String name);

    @Select("select * from 补番目录")
    List<Anime> getAll();
}
