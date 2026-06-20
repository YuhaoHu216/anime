package top.huyuhao.anime.context;

/**
 * 用户上下文 — 基于 ThreadLocal，在同一请求线程内传递当前登录用户信息。
 * 由 JwtInterceptor 在请求进入时设置，请求结束后清除。
 */
public class UserContext {

    private static final ThreadLocal<Integer> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();

    public static void setUserId(Integer userId) {
        userIdHolder.set(userId);
    }

    public static Integer getUserId() {
        return userIdHolder.get();
    }

    public static void setUsername(String username) {
        usernameHolder.set(username);
    }

    public static String getUsername() {
        return usernameHolder.get();
    }

    /**
     * 请求结束后清理，防止内存泄漏
     */
    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
    }
}
