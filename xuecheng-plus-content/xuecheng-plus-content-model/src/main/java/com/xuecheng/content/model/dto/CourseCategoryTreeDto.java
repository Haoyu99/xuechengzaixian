package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/***
 * @title CourseCategoryTreeDto
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/6 19:10
 **/
@Data
public class CourseCategoryTreeDto extends CourseCategory implements
        Serializable {
    List childrenTreeNodes;
}
