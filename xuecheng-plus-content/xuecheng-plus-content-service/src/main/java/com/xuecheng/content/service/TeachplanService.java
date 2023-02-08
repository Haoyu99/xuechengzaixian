package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

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

    public List<TeachplanDto> findTeachplayTree(long courseId);
   /**
    * 新增/修改教学计划
    * @author haoyu99
    * @date 2023/2/7 19:10
    * @param dto

    */

    public void saveTeachplan(SaveTeachplanDto dto);

}
