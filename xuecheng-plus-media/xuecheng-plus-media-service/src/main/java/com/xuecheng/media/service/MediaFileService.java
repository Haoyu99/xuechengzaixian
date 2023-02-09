package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;

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

}
