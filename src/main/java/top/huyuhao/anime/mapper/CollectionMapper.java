package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.Collection;

import java.util.List;

@Mapper
public interface CollectionMapper {

    @Insert("insert into collection(user_id, name, description, is_default, sort_order, is_public) " +
            "values (#{userId}, #{name}, #{description}, #{isDefault}, #{sortOrder}, #{isPublic})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Collection collection);

    @Update("update collection set name = #{name}, description = #{description}, " +
            "is_public = #{isPublic} where id = #{id}")
    void update(Collection collection);

    @Delete("delete from collection where id = #{id}")
    void delete(Integer id);

    @Select("select * from collection where user_id = #{userId} order by is_default desc, sort_order asc")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "isDefault", column = "is_default"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "isPublic", column = "is_public"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Collection> findByUserId(Integer userId);

    @Select("select * from collection where id = #{id}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "isDefault", column = "is_default"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "isPublic", column = "is_public"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Collection findById(Integer id);

    @Select("select * from collection where user_id = #{userId} and is_default = 1 " +
            "and name = #{name} limit 1")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "isDefault", column = "is_default"),
            @Result(property = "sortOrder", column = "sort_order"),
            @Result(property = "isPublic", column = "is_public"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    Collection findDefaultByName(@Param("userId") Integer userId, @Param("name") String name);
}
