package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public CourseTeacher addTeacher(CourseTeacher courseTeacher) {
        //获取传入的教师id
        Long teacherId = courseTeacher.getId();
        //查询表中是否有此id
        CourseTeacher courseTeacher1 = courseTeacherMapper.selectById(teacherId);

        if(courseTeacher1 == null){ // 如果表中不存在 则新增
            int insert = courseTeacherMapper.insert(courseTeacher);
            if(insert != 1){
                XueChengPlusException.cast("添加教师异常，请稍后再试");
            }
        }else {
            //如果表中有此id则进行更新操作
            int i = courseTeacherMapper.updateById(courseTeacher);
            if(i != 1){
                XueChengPlusException.cast("更新教师异常，请稍后再试");
            }

        }


        return courseTeacher;

    }

    /**
     * 删除课程教师
     * @author haoyu99
     * @date 2023/2/21 19:42
     * @param courseId 课程号
     * @param teacherId 教师id

     */

    @Override
    public void deleteTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CourseTeacher::getCourseId,courseId);
        lambdaQueryWrapper.eq(CourseTeacher::getId,teacherId);
        int delete = courseTeacherMapper.delete(lambdaQueryWrapper);
        if(delete != 1){
            XueChengPlusException.cast("删除教师异常，请稍后再试");
        }
    }
}
