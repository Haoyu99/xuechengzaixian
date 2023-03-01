package com.xuecheng.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

/***
 * @title MediaServiceClientFallback
 * @description 媒资服务的降级处理类
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/28 11:28
 **/
public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String upload(MultipartFile upload, String folder, String objectName) {
        //降级方法
        return null;
    }
}
