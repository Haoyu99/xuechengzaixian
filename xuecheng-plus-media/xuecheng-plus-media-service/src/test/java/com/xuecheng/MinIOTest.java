package com.xuecheng;

import com.alibaba.nacos.common.utils.IoUtils;
import io.minio.*;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/***
 * @title MinIOTest
 * @description 测试MinIO上传文件、删除文件、查询文件
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/8 17:01
 **/
public class MinIOTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://123.57.146.121:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    public void testUpload()  {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("1.png")//同一个桶内 对象名不能重复
                    .filename("D:\\DeskTop\\1.png")
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("success");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    //指定桶内的子目录
    public void testUpload2() {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("test/1.png")//同一个桶内 对象名不能重复
                    .filename("D:\\DeskTop\\1.png")
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("success");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testDelete()  {

        RemoveObjectArgs remove = RemoveObjectArgs
                .builder()
                .bucket("testbucket")
                .object("test/1.png")
                .build();
        try {
            minioClient.removeObject(remove);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }

    }
    //查询文件
    @Test
    public void getFile(){
        GetObjectArgs build = GetObjectArgs.builder().bucket("testbucket").object("1.png").build();
        try (GetObjectResponse object = minioClient.getObject(build);
             FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\DeskTop\\2.png"))){

            if(object != null){
                IoUtils.copy(object,fileOutputStream);
            }
            System.out.println(object);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

}
