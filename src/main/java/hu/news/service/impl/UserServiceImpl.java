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
    public void register(User user) {
        userMapper.register(user);
    }
}
