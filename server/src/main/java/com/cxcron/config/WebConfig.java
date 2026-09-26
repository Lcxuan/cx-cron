package com.cxcron.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.cxcron.config.filter.CachedBodyFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Web 相关配置。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/client/**")
                .excludePathPatterns("/client/auth/login", "/client/auth/public-key", "/client/auth/refresh");
    }

    /**
     * 注册跨域过滤器，优先处理浏览器的跨域预检请求。
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return createFilterBean(new CorsFilter(source), Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 注册请求体缓存过滤器。
     */
    @Bean
    public FilterRegistrationBean<CachedBodyFilter> cachedBodyFilter() {
        return createFilterBean(new CachedBodyFilter(), Ordered.HIGHEST_PRECEDENCE + 1);
    }

    /**
     * 创建过滤器注册对象。
     *
     * @param filter 过滤器
     * @param order  执行优先级
     */
    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, int order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }
}
