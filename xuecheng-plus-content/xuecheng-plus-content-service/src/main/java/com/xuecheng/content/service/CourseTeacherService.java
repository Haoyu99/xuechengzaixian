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
   /**
    * 通过课程id查询教师列表
    * @author haoyu99
    * @date 2023/2/21 19:13
    * @param courseID
    * @return List<CourseTeacher>
    */

    List<CourseTeacher> getCourseTeachers(Long courseID);

    /**
     * 新增教师
     * @author haoyu99
     * @date 2023/2/21 19:13
     * @param courseTeacher

     */

    CourseTeacher  addTeacher(CourseTeacher courseTeacher);
}
