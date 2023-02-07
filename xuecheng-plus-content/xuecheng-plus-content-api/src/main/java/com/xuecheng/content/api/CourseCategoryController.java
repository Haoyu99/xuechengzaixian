package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/***
 * @title CourseCategoryController
 * @description 课程分类接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/6 19:03
 **/
@Api(value = "课程分类接口",tags = "课程分类接口")
@RestController
@Slf4j
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;
    @GetMapping("/course-category/tree-nodes")
    @ApiOperation("课程分类查询接口")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return courseCategoryService.queryTreeNodes("1");

    }
}
