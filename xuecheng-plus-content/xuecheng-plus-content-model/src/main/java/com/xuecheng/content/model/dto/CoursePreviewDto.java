package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/***
 * @title CoursePreviewDto
 * @description 课程预览数据模型
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/27 14:16
 **/
@Data
@ToString
public class CoursePreviewDto {

    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;


    //课程计划信息
    List<TeachplanDto> teachplans;

    //师资信息暂时不加...

}
