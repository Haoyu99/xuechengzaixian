package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/***
 * @title MediaServiceClient
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/28 9:50
 **/
@FeignClient(value = "media-api",configuration = MultipartSupportConfig.class ,fallbackFactory = MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {

    @RequestMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestPart("filedata") MultipartFile upload,
                  @RequestParam(value = "folder",required=false) String folder,
                  @RequestParam(value = "objectName",required=false) String objectName);
}

