package com.stock.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.stock.interceptor.AuthorityInterceptor;
import com.stock.interceptor.TokenInterceptor;
import com.stock.interceptor.UserValidInterceptor;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    @Lazy
    private TokenInterceptor tokenInterceptor;
    @Autowired
    @Lazy
    private UserValidInterceptor userValidInterceptor;
    @Autowired
    @Lazy
    private AuthorityInterceptor authorityInterceptor;

    private static final ArrayList<String> PATH_INTERCEPT = new ArrayList<>(4);

    static {
        PATH_INTERCEPT.add("/user/login");
        PATH_INTERCEPT.add("/user/register");
    }

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**").excludePathPatterns(PATH_INTERCEPT);
        registry.addInterceptor(userValidInterceptor).addPathPatterns("/**").excludePathPatterns(PATH_INTERCEPT);
        registry.addInterceptor(authorityInterceptor).addPathPatterns("/**").excludePathPatterns(PATH_INTERCEPT);
    }

    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }

    public static final int PAGE_SIZE = 20;
    public static final String PREFLIGHT = "OPTIONS";

    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true);
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);

        return mybatisPlusInterceptor;
    }

    public static String dataDir;

    public static String flaskUrl;

    @Value("${stock.data.dir}")
    public void setDataDir(String dataDir) {
        WebConfig.dataDir = dataDir;
    }

    @Value("${flask.url}")
    public void setBaseUrl(String flaskUrl) {
        WebConfig.flaskUrl = flaskUrl;
    }
}
