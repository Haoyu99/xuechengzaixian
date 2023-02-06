package com.xuecheng.system.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/***
 * @title GlobalCorsConfig
 * @description  跨域过虑器
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/6 18:25
 **/
@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter getCorsFilter(){
        CorsConfiguration config = new CorsConfiguration();
        //允许白名单域名进行跨域调用 （允许所有）
        config.addAllowedOrigin("*");
        //允许跨越发送cookie
        config.setAllowCredentials(true);
        //放行全部原始头信息
        config.addAllowedHeader("*");
        //允许所有请求方法跨域调用 比如：GET POST 等
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
