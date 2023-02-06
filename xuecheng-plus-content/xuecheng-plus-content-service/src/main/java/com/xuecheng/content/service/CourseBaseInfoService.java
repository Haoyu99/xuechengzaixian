package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
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

    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

}