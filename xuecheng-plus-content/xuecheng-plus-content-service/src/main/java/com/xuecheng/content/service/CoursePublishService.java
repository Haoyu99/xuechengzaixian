package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

/***
 * @title CoursePublishService
 * @description 课程预览、发布接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/27 14:17
 **/
public interface CoursePublishService {

    /**
     * 获取课程预览信息
     * @author haoyu99
     * @date 2023/2/27 14:18
     * @param courseId 课程id
     * @return CoursePreviewDto
     */

    public CoursePreviewDto getCoursePreviewInfo(Long courseId);



}
