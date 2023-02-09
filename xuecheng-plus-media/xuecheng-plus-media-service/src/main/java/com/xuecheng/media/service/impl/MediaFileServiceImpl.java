package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
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
import org.springframework.http.MediaType;
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
      } else if (folder.indexOf("/") < 0) {
          folder = folder + "/";
      }
      //对象名称
      objectName = folder + objectName;
      MediaFiles mediaFiles = null;
      try {
          //上传至文件系统
          addMediaFilesToMinIO(bytes,bucketFile,objectName);
          //写入文件表
          mediaFiles = addMediaFilesToDb(companyId,fileId,dto,bucketFile,objectName);
          UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
          BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
          return uploadFileResultDto;
      } catch (Exception e) {
          e.printStackTrace();
          XueChengPlusException.cast("上传过程中出错");
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

    /** 
     *  将文件写入minIO
     * @author haoyu99
     * @date 2023/2/9 13:45
     * @param bytes  文件字节数组
     * @param bucket 桶
     * @param objectName  对象名称

     */
    
    private void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName){
        try {
            // 资源的媒体类型
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;// 默认未知二进制流类型
            if(objectName.indexOf(".") >= 0){
               //取 objectName 的扩展名
                String extension = objectName.substring(objectName.lastIndexOf("."));
                ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
                if(extensionMatch != null) {
                    String mimeType = extensionMatch.getMimeType();
                }

            }
            //字节输入流
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
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

    private MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,
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
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
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



}
