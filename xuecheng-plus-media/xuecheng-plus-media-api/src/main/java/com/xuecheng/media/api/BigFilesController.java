package com.xuecheng.media.api;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/***
 * @title BigFilesController
 * @description 大文件上传接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/9 16:01
 **/
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {
    @Autowired
    MediaFileService mediaFileService;
    /**
     * 文件上传前检查文件
     * @author haoyu99
     * @date 2023/2/9 16:10
     * @param fileMd5 文件的MD5 值
     * @return RestResponse<Boolean>
     */
    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5) throws Exception {
      return mediaFileService.checkFile(fileMd5);
    }
    /**
     * 分块文件上传前的检测
     * @author haoyu99
     * @date 2023/2/9 16:11
     * @param fileMd5 文件的MD5 值
     * @param chunk chunk的分块值
     * @return RestResponse<Boolean>
     */

    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkChunk(fileMd5,chunk);

    }
    /**
     * 上传分块文件
     * @author haoyu99
     * @date 2023/2/9 16:11
     * @param file 文件
     * @param fileMd5 文件的MD5 值
     * @param chunk chunk的分块值
     * @return RestResponse
     */

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.uploadChunk(fileMd5,chunk,file.getBytes());
    }
    /**
     * 合并文件
     * @author haoyu99
     * @date 2023/2/9 16:12
     * @param fileMd5
     * @param fileName
     * @param chunkTotal
     * @return RestResponse
     */

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        // 封装数据
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("测试视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);
        // 获取文件后缀名
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(fileName);
        String mimeType = extensionMatch.getMimeType();
        uploadFileParamsDto.setContentType(mimeType);

        return mediaFileService.mergechunks(22L,fileMd5,chunkTotal,uploadFileParamsDto);
    }







}