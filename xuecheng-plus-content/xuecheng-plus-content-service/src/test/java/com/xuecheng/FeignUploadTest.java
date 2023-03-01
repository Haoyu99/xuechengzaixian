package com.xuecheng;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/***
 * @title FeignUploadTest
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/28 9:56
 **/
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;

    @Test
    public void test(){
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\xuechengzaixian\\xuecheng-plus-content\\xuecheng-plus-content-service\\src\\test\\resources\\templates\\test.html"));
        String course = mediaServiceClient.upload(multipartFile, "course", "test.html");
        System.out.println(course);
    }
}
