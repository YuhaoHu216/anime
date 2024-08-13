package hu.news.mapper;

import hu.news.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into user(username,password) values (#{username},#{password})")
    void register(String username, String password);

    @Select("select * from user where username = #{username} and password = #{password}")
    User login(String username, String password);
}
