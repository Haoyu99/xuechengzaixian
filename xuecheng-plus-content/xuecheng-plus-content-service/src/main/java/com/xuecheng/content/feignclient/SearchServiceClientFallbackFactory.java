package com.xuecheng.content.feignclient;

import com.xuecheng.content.feignclient.model.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/***
 * @title SearchServiceClientFallbackFactory
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/3/1 12:12
 **/
@Component
@Slf4j

public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {

        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                //降级方法
                log.debug("调用搜索服务时发生熔断，异常信息:{}",throwable.getMessage());
                return null;
            }
        };
    }
}
