package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * @title CourseTeacherServiceImpl
 * @description CourseTeacherService实现类
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/21 18:59
 **/
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> getCourseTeachers(Long courseID) {
        //通过课程id查询教师列表
        LambdaQueryWrapper<CourseTeacher> courseTeacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseTeacherLambdaQueryWrapper.eq(CourseTeacher::getCourseId,courseID);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(courseTeacherLambdaQueryWrapper);
        return courseTeachers;
    }
}
