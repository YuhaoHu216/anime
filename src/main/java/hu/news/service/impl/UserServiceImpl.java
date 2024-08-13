package hu.news.service.impl;

import hu.news.mapper.UserMapper;
import hu.news.pojo.User;
import hu.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public void register(String username, String password) {
        userMapper.register(username,password);
    }

    @Override
    public User login(String username, String password) {
        return userMapper.login(username,password);
    }
}
