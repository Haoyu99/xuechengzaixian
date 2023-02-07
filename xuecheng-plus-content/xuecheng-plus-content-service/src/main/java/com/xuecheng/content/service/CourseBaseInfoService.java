package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/***
 * @title CourseBaseInfoService
 * @description 课程基本信息管理业务接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/6 16:07
 **/
public interface CourseBaseInfoService {
    /**
     * 课程查询接口
     * @author haoyu99
     * @date 2023/2/6 16:08
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 条件条件
     * @return PageResult<CourseBase>
     */

    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);
    /**
     * 新增课程
     * @author haoyu99
     * @date 2023/2/7 10:49
     * @param companyID 培训机构id
     * @param addCourseDto 新增课程id
     * @return CourseBaseInfoDto
     */

    public CourseBaseInfoDto createCourseBase(Long companyID, AddCourseDto addCourseDto);
    /**
     * 根据id查询 返回CourseBaseInfoDto
     * @author haoyu99
     * @date 2023/2/7 14:22
     * @param courseId
     * @return CourseBaseInfoDto
     */

    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) ;
    /**
     * 修改课程信息
     * @author haoyu99
     * @date 2023/2/7 14:28
     * @param companyId 机构id ,要校验本机构只能修改本机构的课程
     * @param dto  EditCourseDto类
     * @return CourseBaseInfoDto
     */

    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);


}
