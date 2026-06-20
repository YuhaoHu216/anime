package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.User;

public interface UserService {

    void register(User user);

    User login(String account, String password);

    User findById(Integer id);

    void updateProfile(User user);

    void updatePassword(Integer id, String oldPassword, String newPassword);
}
