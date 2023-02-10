package com.xuecheng.media.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
 @RestController
public class MediaFilesController {


  @Autowired
  MediaFileService mediaFileService;


 @ApiOperation("媒资列表查询接口")
 @PostMapping("/files")
 public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
  Long companyId = 1232141425L;
  return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);

 }
    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile fileData,
                                      @RequestParam(value = "folder",required=false) String folder,
                                      @RequestParam(value = "objectName",required=false) String objectName)
            throws IOException {
        Long companyId = 22L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        String contentType = fileData.getContentType();
        uploadFileParamsDto.setContentType(contentType);
        uploadFileParamsDto.setFileSize(fileData.getSize());
//        判断content中是否有image
        if(contentType.contains("image")){
            //是个图片
            uploadFileParamsDto.setFileType("001001");
        }else {
            uploadFileParamsDto.setFileType("001003");

        }
        uploadFileParamsDto.setFilename(fileData.getOriginalFilename());
        //这个地方先写死成我自己
        uploadFileParamsDto.setUsername("haoyu99");
        UploadFileResultDto uploadFileResultDto = null;
        try {
             uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, fileData.getBytes(), folder, objectName);
        }catch (Exception e){
            XueChengPlusException.cast("上传文件过程中出错");
        }
        return uploadFileResultDto;

    }

    /**
     * 预览文件的接口
     * @author haoyu99
     * @date 2023/2/10 10:13
     * @param mediaId
     * @return RestResponse<String>
     */

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){
        //调用service 获取String
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        return RestResponse.success(mediaFiles.getUrl());
    }




}
