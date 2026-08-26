package top.huyuhao.anime.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.util.JwtUtil;

/**
 * JWT 拦截器 — 从请求头中提取并校验 JWT token，将用户信息存入 UserContext。
 * 有 token 则解析填充上下文（无论路径是否公开）；非公开路径必须登录，否则返回 401。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** 公开路径（无需登录即可访问；若带有效 token 仍会填充 UserContext） */
    private static final String[] PUBLIC_PATHS = {
            "/user/login",
            "/user/register",
            "/health",
            "/error",
            "/anime/search",
            "/anime/proxy-image",
            "/anime/info/{id}",
            // 评论公开可读（登录态可选）
            "/comment/list",
            "/comment/children",
            // 页面分享（未登录访问他人公开主页）
            "/share/**",
            // Swagger / Knife4j
            "/doc.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/favicon.ico"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = getPath(request);

        // 1) 有 token 就先解析并填充上下文（无论路径是否公开）
        String token = resolveToken(request);
        if (token != null && !token.isBlank() && jwtUtil.validateToken(token)) {
            Integer userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            log.info("JWT 验证通过 — 请求路径: {}, userId: {}, username: {}", path, userId, username);
            UserContext.setUserId(userId);
            UserContext.setUsername(username);
        }

        // 2) 文件读取（GET /file/** 图片等静态资源）无需认证，但上传（POST）仍需走校验
        if ("GET".equals(request.getMethod()) && path.startsWith("/file/")) {
            return true;
        }

        // 3) 公开路径：放行（此时若带有效 token，上下文已填充）
        for (String p : PUBLIC_PATHS) {
            if (pathMatcher.match(p, path)) {
                return true;
            }
        }

        // 4) 其余必须登录
        if (UserContext.getUserId() != null) {
            return true;
        }

        // 未登录，返回 401
        log.warn("JWT 验证失败 — 请求路径: {}, token是否存在: {}", path, token != null && !token.isBlank());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":0,\"msg\":\"未登录或登录已过期\"}");
        return false;
    }

    /** 提取去除 context-path 后的请求路径，如 /anime/info/1 */
    private String getPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx)) {
            return uri.substring(ctx.length());
        }
        return uri;
    }

    /** 从 Authorization 头或 query 参数中提取 token */
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
        }
        // 也支持从 query 参数中获取（用于 WebSocket 等场景）
        if (token == null || token.isBlank()) {
            token = request.getParameter("token");
        }
        return token;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
