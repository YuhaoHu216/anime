package top.huyuhao.anime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import top.huyuhao.anime.mapper.CollectionMapper;
import top.huyuhao.anime.mapper.UserMapper;
import top.huyuhao.anime.pojo.Collection;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.User;
import top.huyuhao.anime.pojo.dto.UserRegisterDTO;
import top.huyuhao.anime.service.UserService;
import top.huyuhao.anime.util.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Result register(UserRegisterDTO dto) {
        // 检查账号是否已存在
        User existing = userMapper.findByAccount(dto.getAccount());
        if (existing != null) {
            throw new RuntimeException("账号已存在");
        }
        // DTO 转实体
        User user = dto.toUser();
        // BCrypt 加密密码
        user.setPassword(encoder.encode(user.getPassword()));
        userMapper.register(user);
        // 为用户创建 5 个默认收藏夹
        createDefaultCollections(user.getId());
        return Result.success("注册成功");
    }

    @Override
    public Result login(String account, String password) {
        User user = userMapper.findByAccount(account);
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 生成并返回 JWT token
        String token =  jwtUtil.generateToken(user.getId(), user.getUsername());
        return Result.success("success",token);
    }

    @Override
    public User findById(Integer id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public User getCurrentUser(Integer id) {
        return findById(id);
    }

    @Override
    public void updateProfile(User user) {
        userMapper.updateProfile(user);
    }

    @Override
    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        userMapper.updatePassword(id, encoder.encode(newPassword));
    }

    private void createDefaultCollections(Integer userId) {
        String[] defaultNames = {"想看", "在看", "看过", "搁置", "抛弃"};
        int sort = 1;
        for (String name : defaultNames) {
            Collection c = new Collection();
            c.setUserId(userId);
            c.setName(name);
            c.setIsDefault(true);
            c.setSortOrder(sort++);
            c.setIsPublic(false);
            collectionMapper.insert(c);
        }
    }
}
