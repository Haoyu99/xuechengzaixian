package com.xuecheng;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/***
 * @title BigFileTest
 * @description 测试大文件分块与合并
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/9 15:06
 **/
public class BigFileTest {
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("D:\\DeskTop\\2.png");

        //分块文件存储目录
        File chuckFolderPath = new File("D:\\DeskTop\\chunk") ;
        if(!chuckFolderPath.exists()){
            chuckFolderPath.mkdir();
        }
        //分块大小
        int chuckSize = 1024 * 1024 * 1; //1M
        //分块数量
        long chuckNum = (long) Math.ceil(sourceFile.length() * 1.0 / chuckSize);

        //使用流对象读取文件，再向分块文件写数据，达到分块大小不写

        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //新建缓冲区
        byte[] b = new byte[1024];
        for (long i = 0; i < chuckNum; i++) {
            File file = new File("D:\\DeskTop\\chunk\\" + i);
            if(file.exists()){
                file.delete();
            }
            boolean newFile = file.createNewFile();
            RandomAccessFile raf_write = new RandomAccessFile(file,"rw");

            if(newFile){
                //向分块模块写对象的流对象
                int len ;
                while((len = raf_read.read(b)) != -1){
                    //向文件中写数据
                    raf_write.write(b,0,len);
                    //达到分块大小不再写
                    if(file.length() >= chuckSize){
                        break;
                    }
                }
            }
            raf_write.close();
        }
        raf_read.close();

    }

    @Test
    public void testMerge() throws IOException {
        //源文件
        File sourceFile = new File("D:\\DeskTop\\2.png");

        //分块文件存储目录
        File chuckFolderPath = new File("D:\\DeskTop\\chunk") ;
        if(!chuckFolderPath.exists()){
            chuckFolderPath.mkdir();
        }
        //合并后的文件
        File mergeFile = new File("D:\\DeskTop\\888.png");
        boolean newFile = mergeFile.createNewFile();

        // 使用流数据读取文件，向合并文件写数据
        // 获取分块文件列表
        File[] chuckFiles = chuckFolderPath.listFiles();
        List<File> chuckFilesList = Arrays.asList(chuckFiles);
        System.out.println(chuckFilesList);
        //按照升序排列
        Collections.sort(chuckFilesList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName()) ;
            }
        });
        //创建合并文件的流数据
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        byte[] bytes = new byte[1024];
        for (File file: chuckFilesList) {
            //读取分块文件的流对象
            RandomAccessFile raf_read = new RandomAccessFile(file, "r");
            int len;
            while((len = raf_read.read(bytes)) != -1){
                //向合并文件写
                raf_write.write(bytes,0,len);
            }
        }

        //校验合并后的文件是否一样
        FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        String sourceFileMd5 = DigestUtils.md5Hex(sourceFileStream);
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
        if(sourceFileMd5.equals(mergeFileMd5)){
            System.out.println("success");
        }else {
            System.out.println("fail");
        }

    }
}
