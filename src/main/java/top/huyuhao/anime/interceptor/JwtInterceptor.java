package top.huyuhao.anime.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.util.JwtUtil;

/**
 * JWT 拦截器 — 从请求头中提取并校验 JWT token，将用户信息存入 UserContext。
 * 校验失败返回 401。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 Authorization 请求头获取 token
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
        }

        // 也支持从 query 参数中获取（用于 WebSocket 等场景）
        if (token == null || token.isBlank()) {
            token = request.getParameter("token");
        }

        if (token != null && !token.isBlank() && jwtUtil.validateToken(token)) {
            // 验证通过，将用户信息存入 ThreadLocal
            UserContext.setUserId(jwtUtil.getUserIdFromToken(token));
            UserContext.setUsername(jwtUtil.getUsernameFromToken(token));
            return true;
        }

        // 验证失败，返回 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":0,\"msg\":\"未登录或登录已过期\"}");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
