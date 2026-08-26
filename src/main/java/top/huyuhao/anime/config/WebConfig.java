package top.huyuhao.anime.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.huyuhao.anime.interceptor.JwtInterceptor;

/**
 * Web MVC 配置 — 注册 JWT 拦截器（白名单已在拦截器内部处理）
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求，公开路径由拦截器内部放行
                .addPathPatterns("/**");
    }
}
