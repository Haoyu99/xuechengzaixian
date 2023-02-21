package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/***
 * @title CourseBaseInfoController
 * @description 课程信息编辑接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/3 17:20
 **/
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    /**
     *
     * @author haoyu99
     * @date 2023/2/3 17:27
     * @param pageParams 分页查询通用参数 以key-value 形式传递
     * @param queryCourseParams 课程查询参数Dto 以JSon格式 传输 所以要加RequestBody注解
     * @return PageResult<CourseBase> 返回对象 用于前端解析显示
     */
    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody
    QueryCourseParamsDto queryCourseParams){
        //调用service获取数据
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParams);
        return courseBasePageResult;
    }
    /**
     * 新增课程基础信息
     * @author haoyu99
     * @date 2023/2/7 11:27
     * @param addCourseDto 网页填写的表单内容对象
     * @return CourseBaseInfoDto
     */

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    //@Validated  增加校验功能 而不是在Service中手动校验
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto){
        // 获取当前用户所属的培训机构id
        Long companyId = 22L;
        //调用service
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);

        return courseBase;
    }
    /**
     * 根据id查询课程
     * @author haoyu99
     * @date 2023/2/7 14:04
     * @param courseId
     * @return CourseBaseInfoDto
     */
    @ApiOperation("根据课程id查询课程基础信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }


    /**
     * 修改课程基础信息接口
     * @author haoyu99
     * @date 2023/2/7 14:16
     * @param editCourseDto
     * @return CourseBaseInfoDto
     */
    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        Long companyId = 22L;

        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }

    @DeleteMapping("/course/{courseID}")
    public void deleteCourse(@PathVariable Long courseID){
        courseBaseInfoService.deleteCourse(courseID);
    }

}
