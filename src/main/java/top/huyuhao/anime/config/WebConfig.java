package top.huyuhao.anime.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.huyuhao.anime.interceptor.JwtInterceptor;

/**
 * Web MVC 配置 — 注册 JWT 拦截器，配置白名单
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 白名单：登录、注册、健康检查、Swagger/Knife4j 文档
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/health",
                        "/error",
                        // Swagger / Knife4j
                        "/doc.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico"
                );
    }
}
