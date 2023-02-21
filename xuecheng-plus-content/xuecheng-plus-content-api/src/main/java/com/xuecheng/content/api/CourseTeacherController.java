package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/courseTeacher/list/{courseID}")
    public List<CourseTeacher> getCourseTeachers(@PathVariable Long courseID){
        return courseTeacherService.getCourseTeachers(courseID);
    }
}
