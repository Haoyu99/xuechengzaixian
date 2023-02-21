package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/***
 * @title CourseTeacherService
 * @description 课程教师相关服务
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/21 18:57
 **/
public interface CourseTeacherService {
    List<CourseTeacher> getCourseTeachers(Long courseID);
}
