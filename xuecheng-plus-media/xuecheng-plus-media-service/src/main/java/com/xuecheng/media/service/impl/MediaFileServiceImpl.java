package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
 @Service
 @Slf4j
public class MediaFileServiceImpl implements MediaFileService {

  @Autowired
  MediaFilesMapper mediaFilesMapper;

  @Autowired
  MinioClient minioClient;

  @Autowired
          //代理对象
  MediaFileService currentProxy;


    //获取桶的名字
  @Value("${minio.bucket.files}")
  private String bucketFile;
  @Value("${minio.bucket.videofiles}")
  private String videoBucket;
  @Override
  public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
    LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  //添加条件
    queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()),MediaFiles::getFilename,queryMediaParamsDto.getFilename());
    queryWrapper.eq(StringUtils.isNotEmpty(queryMediaParamsDto.getFileType()),MediaFiles::getFileType,queryMediaParamsDto.getFileType());

  //分页对象
    Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
  // 查询数据内容获得结果
    Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
  // 获取数据列表
    List<MediaFiles> list = pageResult.getRecords();
  // 获取数据总数
    long total = pageResult.getTotal();
  // 构建结果集
    PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
    return mediaListResult;

 }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto dto, byte[] bytes, String folder, String objectName) {
      //生成文件id，文件的md5值
      String fileId = DigestUtils.md5Hex(bytes);
      //文件名称
      String filename = dto.getFilename();
      //构造objectname
      if (StringUtils.isEmpty(objectName)) {
          objectName = fileId + filename.substring(filename.lastIndexOf("."));
      }
      if (StringUtils.isEmpty(folder)) {
          //通过日期构造文件存储路径
          folder = getFileFolder(new Date(), true, true, true);
      } else if (!folder.contains("/")) {
          folder = folder + "/";
      }
      //对象名称
      objectName = folder + objectName;
      MediaFiles mediaFiles = null;
      try {
          //上传至文件系统
          addMediaFilesToMinIO(bytes,bucketFile,objectName);
          //写入文件表
          mediaFiles = currentProxy.addMediaFilesToDb(companyId,fileId,dto,bucketFile,objectName);
          UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
          BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
          return uploadFileResultDto;
      } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e.getMessage());
      }
//      return null;

  }

 /**
  * 根据日期拼接目录
  * @author haoyu99
  * @date 2023/2/9 10:27
  * @param date
  * @param year
  * @param month
  * @param day
  * @return String
  */

   private String getFileFolder(Date date, boolean year, boolean month, boolean day){
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
     //获取当前日期字符串
     String dateString = sdf.format(date);
     //取出年、月、日
     String[] dateStringArray = dateString.split("-");
     StringBuffer folderString = new StringBuffer();
     if(year){
      folderString.append(dateStringArray[0]);
      folderString.append("/");
     }
     if(month){
      folderString.append(dateStringArray[1]);
      folderString.append("/");
     }
     if(day){
      folderString.append(dateStringArray[2]);
      folderString.append("/");
     }
     return folderString.toString();
    }

    /** 
     *  将文件写入minIO
     * @author haoyu99
     * @date 2023/2/9 13:45
     * @param bytes  文件字节数组
     * @param bucket 桶
     * @param objectName  对象名称

     */
    
    private void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName){
            // 资源的媒体类型
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;// 默认未知二进制流类型
            if(objectName.indexOf(".") >= 0){
               //取 objectName 的扩展名
                String extension = objectName.substring(objectName.lastIndexOf("."));
                ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
                if(extensionMatch != null) {
                    contentType = extensionMatch.getMimeType();
                }

            }
        try (//字节输入流
             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);) {

            //上传到minio
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    //InputStream stream, long objectSize, long partSize 分片大小（-1 表示5m，最大不要超过5T，最多10000）
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("上传文件到文件系统出错{}",e.getMessage());
            XueChengPlusException.cast("上传文件到文件系统出错");
        }
    }

    //将文件上传到minIO，传入文件绝对路径
    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .filename(filePath)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传文件到文件系统出错");
        }
    }

    /**
     * 文件信息入库
     * @author haoyu99
     * @date 2023/2/9 14:09
     * @param companyId  公司id
     * @param fileMd5  文件唯一id
     * @param uploadFileParamsDto  文件信息dto
     * @param bucket 桶
     * @param objectName 文件名称
     * @return MediaFiles
     */
    @Override
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,
                                        String bucket,String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            // 这里需要判断文件类型 再设置URL
            // 获取扩展名
            String extension = null;
            String filename = uploadFileParamsDto.getFilename();
            if(StringUtils.isNotEmpty(filename) && filename.contains(".")){
                extension = filename.substring(filename.lastIndexOf("."));
            }
            String mimeType = getMimeTypeByExtension(extension);

            //图片 mp4 直接设置 其他不行
            if(mimeType.contains("image") || mimeType.contains("mp4")){
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }
            mediaFiles.setBucket(bucket);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setFilePath(objectName);
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                XueChengPlusException.cast("保存文件信息失败");
            }

        }
        return mediaFiles;
    }

    @Override
    /**
     * 检查文件是否存在
     * @author haoyu99
     * @date 2023/2/9 16:42
     * @param fileMd5
     * @return RestResponse<Boolean>
     */

    public RestResponse<Boolean> checkFile(String fileMd5) {
        //在文件表存在，并且在文件系统存在，此文件才存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles==null){
            return RestResponse.success(false);
        }
        //查看是否在文件系统存在
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(mediaFiles.getBucket()).object(mediaFiles.getFilePath()).build();
        InputStream  inputStream = null;
        try {
             inputStream = minioClient.getObject(getObjectArgs);
            if(inputStream==null){
                //文件不存在
                return RestResponse.success(false);
            }
        }catch (Exception e){
            //文件不存在
            return RestResponse.success(false);
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        //文件已存在
        return RestResponse.success(true);
    }

    @Override
    /**
     * 检查分块是否存在
     * @author haoyu99
     * @date 2023/2/9 16:44
     * @param fileMd5
     * @param chunkIndex
     * @return RestResponse<Boolean>
     */

    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        //文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(videoBucket)
                            .object(chunkFilePath)
                            .build());

            if (fileInputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //分块未存在
        return RestResponse.success(false);
    }

   /**
    * 上传分块文件
    * @author haoyu99
    * @date 2023/2/9 16:58
    * @param fileMd5
    * @param chunk
    * @param bytes
    * @return RestResponse
    */
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {

        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            //将文件存储至minIO
            addMediaFilesToMinIO(bytes, videoBucket,chunkFilePath);

        } catch (Exception ex) {
            ex.printStackTrace();
            XueChengPlusException.cast("上传过程出错请重试");
        }
        return RestResponse.success();

    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //下载分块
        String fileName = uploadFileParamsDto.getFilename();
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);
        //扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //创建临时文件作为合并文件
        File mergeFile = null;

        try {
            mergeFile = File.createTempFile(fileMd5, extName);
        } catch (IOException e) {
            XueChengPlusException.cast("合并文件过程中创建临时文件出错");
        }
        try {
            //开始合并
            byte[] b = new byte[1024];
            try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");) {
                for (File chunkFile : chunkFiles) {
                    try (FileInputStream chunkFileStream = new FileInputStream(chunkFile);) {
                        int len = -1;
                        while ((len = chunkFileStream.read(b)) != -1) {
                            //向合并后的文件写
                            raf_write.write(b, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                XueChengPlusException.cast("合并文件过程中出错");
            }
            log.debug("合并文件完成{}", mergeFile.getAbsolutePath());
            uploadFileParamsDto.setFileSize(mergeFile.length());

            try (InputStream mergeFileInputStream = new FileInputStream(mergeFile);) {
                //对文件进行校验，通过比较md5值
                String newFileMd5 = DigestUtils.md5Hex(mergeFileInputStream);
                if (!fileMd5.equalsIgnoreCase(newFileMd5)) {
                    //校验失败
                    XueChengPlusException.cast("合并文件校验失败");
                }
                log.debug("合并文件校验通过{}", mergeFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                //校验失败
                XueChengPlusException.cast("合并文件校验异常");
            }
            //将临时文件上传至minio
            String mergeFilePath = getFilePathByMd5(fileMd5, extName);
            try {

                //上传文件到minIO

                addMediaFilesToMinIO(mergeFile.getAbsolutePath(), videoBucket, mergeFilePath);
                log.debug("合并文件上传MinIO完成{}",mergeFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                XueChengPlusException.cast("合并文件时上传文件出错");
            }

            //入数据库
            MediaFiles mediaFiles = addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, videoBucket, mergeFilePath);
            if (mediaFiles == null) {
                XueChengPlusException.cast("媒资文件入库出错");
            }

            return RestResponse.success();
        } finally {
            //删除临时文件
            for (File file : chunkFiles) {
                try {
                    file.delete();
                } catch (Exception e) {

                }
            }
            try {
                mergeFile.delete();
            } catch (Exception e) {

            }
        }
    }

   /**
    * 根据id查询文件信息
    * @author haoyu99
    * @date 2023/2/10 11:03
    * @param id
    * @return MediaFiles
    */

    @Override
    public MediaFiles getFileById(String id) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(id);
        if(mediaFiles == null){
            XueChengPlusException.cast("文件不存在");
        }
        String url = mediaFiles.getUrl();
        if(url.isEmpty()){
            XueChengPlusException.cast("文件还未处理请稍后预览");
        }
        return mediaFiles;
    }

    /**
     * 根据md5 得到分块文件的目录  MD5的第一位/md5的第二位/md5值
     * @author haoyu99
     * @date 2023/2/9 16:50
     * @param fileMd5
     * @return String
     */

    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }
    /**
     * 下载文件的所有分块
     * @author haoyu99
     * @date 2023/2/9 17:18
     * @param fileMd5
     * @param chunkTotal
     * @return File[]
     */

    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File[] files = new File[chunkTotal];
        //检查分块文件是否上传完毕
        for (int i = 0; i < chunkTotal; i++) {
            String chunkFilePath = chunkFileFolderPath + i;
            //下载文件
            File chunkFile =null;
            try {
                chunkFile = File.createTempFile("chunk" + i, null);
            } catch (IOException e) {
                e.printStackTrace();
                XueChengPlusException.cast("下载分块时创建临时文件出错");
            }
            downloadFileFromMinIO(chunkFile,videoBucket,chunkFilePath);
            files[i]=chunkFile;
        }
        return files;
    }

    /**
     * 从MinIo下载文件
     * @author haoyu99
     * @date 2023/2/9 17:19
     * @param file
     * @param bucket
     * @param objectName
     */

    private File downloadFileFromMinIO(File file, String bucket, String objectName) {
        InputStream fileInputStream = null;
        OutputStream fileOutputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            try {
                fileOutputStream = new FileOutputStream(file);
                IOUtils.copy(fileInputStream, fileOutputStream);

            } catch (IOException e) {
                XueChengPlusException.cast("下载文件"+objectName+"出错");
            }
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("文件不存在"+objectName);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;

    }



   /**
    * 通过MD5值得到文件的路径
    * @author haoyu99
    * @date 2023/2/10 10:40
    * @param fileMd5
    * @param fileExt
    * @return String
    */
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

    /**
     * 根据扩展名拿匹配的媒体类型
     * @author haoyu99
     * @date 2023/2/10 10:42
     * @param extension
     * @return String
     */
    private String getMimeTypeByExtension(String extension) {
        // 资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;// 默认未知二进制流类型
        if(StringUtils.isNotEmpty(extension)){
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }




}
