package com.xuecheng.model.dto;

import lombok.Data;
import lombok.ToString;

/***
 * @title QueryCourseParamsDto
 * @description 课程查询参数Dto
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/3 16:54
 **/
@Data
@ToString
public class QueryCourseParamsDto {
    //审核状态

    private String auditStatus;
    //课程名称

    private String courseName;
    //发布状态

    private String publishStatus;
}
