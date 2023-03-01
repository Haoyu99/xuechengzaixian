package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/***
 * @title CoursePublishController
 * @description 课程预览，发布
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/27 11:13
 **/
@Controller
@Api(value = "课程预览发布接口",tags = "课程预览发布接口")
public class CoursePublishController {
    @Autowired
    CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model",coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    /**
     * 提交审核接口
     * @author haoyu99
     * @date 2023/2/27 16:02
     * @param courseId

     */

    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 22L;
        coursePublishService.commitAudit(companyId,courseId);
    }

    /**
     * 课程发布接口
     * @author haoyu99
     * @date 2023/2/27 17:08
     * @param courseId  课程id
     */

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursePublish(@PathVariable("courseId") Long courseId){
        Long companyId = 22L;
        coursePublishService.publish(companyId,courseId);
    }

}
