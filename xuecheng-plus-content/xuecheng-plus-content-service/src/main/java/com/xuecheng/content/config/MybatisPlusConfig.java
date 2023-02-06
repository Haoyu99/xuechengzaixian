package com.xuecheng.content.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * @title MybatisPlusConfig
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/6 14:28
 **/
@Configuration
@MapperScan("com.xuecheng.content.mapper")
public class MybatisPlusConfig {

    /** 
     *  定义分页拦截器
     * @author haoyu99
     * @date 2023/2/6 14:31
     * @return MybatisPlusInterceptor 
     */
    
    @Bean
    public MybatisPlusInterceptor getMybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
