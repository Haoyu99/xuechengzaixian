package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/***
 * @title EditCourseDto
 * @description 修改课程提交的dto
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/7 14:14
 **/
@Data
@ToString
@ApiModel(value="EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto{

    @ApiModelProperty(value = "课程名称", required = true)
    private Long id;

}
