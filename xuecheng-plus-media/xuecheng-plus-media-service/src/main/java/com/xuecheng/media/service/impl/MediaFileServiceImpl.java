package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
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

//获取桶的名字
  @Value("${minio.bucket.files}")
  private String bucketFile;
  @Override
  public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
    LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  
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

  @Transactional
  @Override
  public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto dto, byte[] bytes, String folder, String objectName) {
   //得到文件的md5值
   String fileMd5 = DigestUtils.md5Hex(bytes);

   if(StringUtils.isEmpty(folder)){
   //自动生成给目录路径 按照 年 月 日生成
          folder = getFileFolder(new Date(),true, true, true);
    }else if(folder.indexOf("/") < 0){
          folder += "/";
    }
   String filename = dto.getFilename();
   if(StringUtils.isEmpty(objectName)){
     //如果objectName为空 ，使用文件的MD5值
     objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
    }
   try {
   //获取文件类型
     String contentType = dto.getContentType();
   //字节输入流
     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
   //上传到minio
     PutObjectArgs putObjectArgs = PutObjectArgs.builder()
           .bucket(bucketFile)
           .object(folder+"/"+objectName)
           //InputStream stream, long objectSize, long partSize 分片大小（-1 表示5m，最大不要超过5T，最多10000）
           .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
           .contentType(contentType)
           .build();
     minioClient.putObject(putObjectArgs);

    //保存到数据库
    // fileMd5是主键
    MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
    if(mediaFiles == null){
     //插入文件表
      mediaFiles = new MediaFiles();
      //封装数据
     BeanUtils.copyProperties(dto,mediaFiles);
     mediaFiles.setId(fileMd5);
     mediaFiles.setFileId(fileMd5);
     mediaFiles.setCompanyId(companyId);
     mediaFiles.setBucket(bucketFile);
     mediaFiles.setFilePath(objectName);
     mediaFiles.setUrl("/"+ bucketFile + "/" + objectName);
     mediaFiles.setCreateDate(LocalDateTime.now());
     mediaFiles.setStatus("1");
     mediaFiles.setAuditStatus("00203");
        //插入数据
        mediaFilesMapper.insert(mediaFiles);
    }
       //准备返回数据
       UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
       BeanUtils.copyProperties(dto,uploadFileResultDto);
       return uploadFileResultDto;

   } catch (Exception e) {
       log.debug("上传文件文件失败,{}",e.getMessage());
//       throw new XueChengPlusException("上传文件文件失败");
    }
         return null;

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



}
