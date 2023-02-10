package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


/**
 * 上传文件通用接口
 * @author haoyu99
 * @date 2023/2/9 9:59
 * @param companyId  机构id
 * @param dto 上传文件参数dto
 * @param bytes 上传文件字节数组
 * @param folder 桶下的子目录
 * @param objectName  对象名称
 * @return UploadFileResultDto
 */

 public UploadFileResultDto uploadFile(Long companyId,  UploadFileParamsDto dto,byte [] bytes ,String folder,String objectName);

 /**
  *  将文件信息添加到文件表
  * @author haoyu99
  * @date 2023/2/9 14:38
  * @param companyId
  * @param fileMd5
  * @param uploadFileParamsDto
  * @param bucket
  * @param objectName
  * @return MediaFiles
  */
 @Transactional
 public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName);
/**
 * 检查文件是否存在
 * @author haoyu99
 * @date 2023/2/9 16:39
 * @param fileMd5
 * @return RestResponse<Boolean>
 */

public RestResponse<Boolean> checkFile(String fileMd5);
/**
 * 检查分块是否存在
 * @author haoyu99
 * @date 2023/2/9 16:40
 * @param fileMd5
 * @param chunkIndex
 * @return RestResponse<Boolean>
 */

public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);
 /**
  * 上传分块
  * @author haoyu99
  * @date 2023/2/9 16:41
  * @param fileMd5
  * @param chunk
  * @param bytes
  * @return RestResponse
  */

 public RestResponse uploadChunk(String fileMd5,int chunk,byte[] bytes);
 /**
  *  合并分块
  * @author haoyu99
  * @date 2023/2/9 16:41
  * @param companyId
  * @param fileMd5
  * @param chunkTotal
  * @param uploadFileParamsDto
  * @return RestResponse
  */

 public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);

 /**
  * 根据id查询文件信息
  * @author haoyu99
  * @date 2023/2/10 11:02
  * @param id
  * @return MediaFiles
  */

 public MediaFiles getFileById(String id);

}
