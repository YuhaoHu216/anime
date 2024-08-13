package hu.news.service;

import hu.news.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void register(String username, String password);

    User login(String username, String password);
}
