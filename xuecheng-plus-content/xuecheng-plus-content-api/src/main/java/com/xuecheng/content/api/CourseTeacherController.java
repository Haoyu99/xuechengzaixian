package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
 * @title CourseTeacherController
 * @description 课程教师接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/21 18:53
 **/
@Api(value = "课程教师接口",tags = "课程教师接口")
@RestController
@Slf4j
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;

   /**
    * 课程教师查询接口（通过课程id查询教师列表）
    * @author haoyu99
    * @date 2023/2/21 19:09
    * @param courseID  课程id
    * @return List<CourseTeacher>
    */
    @GetMapping("/courseTeacher/list/{courseID}")
    public List<CourseTeacher> getCourseTeachers(@PathVariable Long courseID){
        return courseTeacherService.getCourseTeachers(courseID);
    }

    /**
     * 新增 修改教师接口
     * @author haoyu99
     * @date 2023/2/21 19:22
     * @param courseTeacher 教师实体类
     * @return CourseTeacher
     */

    @PostMapping("/courseTeacher")
    public CourseTeacher addTeacher(@RequestBody CourseTeacher courseTeacher){
        System.out.println("a");
        return courseTeacherService.addTeacher(courseTeacher);
    }


}
