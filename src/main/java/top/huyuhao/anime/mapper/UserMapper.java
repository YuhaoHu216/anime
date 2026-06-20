package top.huyuhao.anime.mapper;

import org.apache.ibatis.annotations.*;
import top.huyuhao.anime.pojo.User;

@Mapper
public interface UserMapper {

    @Select("select * from user where account = #{account}")
    User findByAccount(String account);

    @Select("select * from user where id = #{id}")
    User findById(Integer id);

    @Insert("insert into user(username, account, email, password, phone_number) " +
            "values (#{username}, #{account}, #{email}, #{password}, #{phoneNumber})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void register(User user);

    @Update("update user set username = #{username}, email = #{email}, " +
            "avatar_url = #{avatarUrl}, phone_number = #{phoneNumber} where id = #{id}")
    void updateProfile(User user);

    @Update("update user set password = #{password} where id = #{id}")
    void updatePassword(@Param("id") Integer id, @Param("password") String password);
}
