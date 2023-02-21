package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/***
 * @title TeachplanService
 * @description 查询课程计划树型结构
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/7 17:12
 **/
public interface TeachplanService {
   /**
    * 查看教学计划
    * @author haoyu99
    * @date 2023/2/7 19:11
    * @param courseId
    * @return List<TeachplanDto>
    */

    public List<TeachplanDto> findTeachplanTree(long courseId);


    /**
     * 新增/修改教学计划
     * @author haoyu99
     * @date 2023/2/7 19:10
     * @param dto

     */

    public void saveTeachplan(SaveTeachplanDto dto);

    /**
     *  删除教学计划
     * @author haoyu99
     * @date 2023/2/21 17:22
     * @param id

     */

    public void deleteTeachplan(Long id);


    /**
     * 教学计划绑定媒资
     * @author haoyu99
     * @date 2023/2/13 16:53
     * @param bindTeachplanMediaDto
     * @return TeachplanMedia
     */

    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
    /** 
     * 删除教学计划与媒资之间的绑定关系
     * @author haoyu99
     * @date 2023/2/13 16:53
     * @param mediaId  媒资文件id

     */

    public void delAssociationMedia( String mediaId);

}
