package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/***
 * @title CourseCategoryService
 * @description 课程分类树形结构查询
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/6 23:01
 **/
public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
