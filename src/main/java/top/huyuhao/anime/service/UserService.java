package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.User;
import top.huyuhao.anime.pojo.dto.UserRegisterDTO;

public interface UserService {

    Result register(UserRegisterDTO dto);

    /**
     * 用户登录，返回 JWT token
     */
    Result login(String account, String password);

    User findById(Integer id);

    User getCurrentUser(Integer id);

    void updateProfile(User user);

    void updatePassword(Integer id, String oldPassword, String newPassword);
}
