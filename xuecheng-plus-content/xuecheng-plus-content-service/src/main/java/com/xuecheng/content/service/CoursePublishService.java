package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

import java.io.File;

/***
 * @title CoursePublishService
 * @description 课程预览、发布接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/27 14:17
 **/
public interface CoursePublishService {

    /**
     * 获取课程预览信息
     * @author haoyu99
     * @date 2023/2/27 14:18
     * @param courseId 课程id
     * @return CoursePreviewDto
     */

    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

   /**
    *  提交审核
    * @author haoyu99
    * @date 2023/2/27 16:04
    * @param companyId 公司id
    * @param courseId 课程id

    */

    public void commitAudit(Long companyId,Long courseId);

    /**
     * 课程发布
     * @author haoyu99
     * @date 2023/2/27 17:14
     * @param companyId 机构id
     * @param courseId 课程id

     */

    public void publish(Long companyId,Long courseId);

    /**
     * 课程静态化
     * @author haoyu99
     * @date 2023/2/28 14:18
     * @param courseId
     * @return File
     */

    public File generateCourseHtml(Long courseId);

    /**
     * 上传课程静态页面
     * @author haoyu99
     * @date 2023/2/28 14:18
     * @param courseId
     * @param file

     */

    public void uploadCourseHtml(Long courseId, File file);
    



}
