package hu.news.mapper;

import hu.news.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    @Insert("insert into user(username,password) values (#{username},#{password})")
    void register(User user);
}
