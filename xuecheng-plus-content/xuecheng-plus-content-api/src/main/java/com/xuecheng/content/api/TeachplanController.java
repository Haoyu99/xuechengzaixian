package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
 * @title TeachplanController
 * @description  课程计划编辑接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/7 16:27
 **/
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;

    /**
     * 查询课程计划树形结构 课程计划 以及 关联的媒体资源
     * @author haoyu99
     * @date 2023/2/7 16:28
     * @param courseId
     * @return List<TeachplanDto>
     */
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType
            = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.findTeachplanTree(courseId);
    }

    @PostMapping("teachplan")
    /** 
     * 
     * @author haoyu99
     * @date 2023/2/7 19:11
     * @param dto 
     
     */
    
    public void saveTeachplan(@RequestBody SaveTeachplanDto dto){
           teachplanService.saveTeachplan(dto);
    }
    /**
     *  删除课程计划
     * @author haoyu99
     * @date 2023/2/21 16:46
     * @param id  课程计划id
     * @return RestResponse<Boolean>
     */

    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachplan(@PathVariable Long teachplanId){
         teachplanService.deleteTeachplan(teachplanId);

    }

    /**
     * 课程计划和媒资信息绑定
     * @author haoyu99
     * @date 2023/2/13 16:51
     * @param bindTeachplanMediaDto 计划-媒资 DTO

     */

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);

    }
    /**
     * 课程计划和媒资信息解除绑定
     * @author haoyu99
     * @date 2023/2/13 16:52
     * @param mediaId

     */

    @ApiOperation(value = "课程计划和媒资信息解除绑定")
    @DeleteMapping("/teachplan/association/media/{mediaId}")
    public void delAssociationMedia(@PathVariable String mediaId){
        teachplanService.delAssociationMedia(mediaId);
    }

}
