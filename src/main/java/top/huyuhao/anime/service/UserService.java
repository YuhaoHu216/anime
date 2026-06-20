package top.huyuhao.anime.service;

import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.User;
import top.huyuhao.anime.pojo.dto.UserRegisterDTO;
import top.huyuhao.anime.pojo.dto.UserUpdateDTO;

public interface UserService {

    Result register(UserRegisterDTO dto);

    /**
     * 用户登录，返回 JWT token
     */
    Result login(String account, String password);

    Result getCurrentUser(Integer id);

    Result updateProfile(Integer userId, UserUpdateDTO dto);

    Result updatePassword(Integer id, String oldPassword, String newPassword);
}
