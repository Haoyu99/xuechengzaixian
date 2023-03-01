package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/*** 课程发布任务
 * @title CoursePublishTask
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/27 17:50
 **/
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    CoursePublishService coursePublishService;
    //课程发布消息类型
    public static final String MESSAGE_TYPE = "course_publish";

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,MESSAGE_TYPE,5,60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息相关的业务信息
        log.debug("开始执行课程发布任务...id = {}",mqMessage.getBusinessKey1());

        long courseId = Long.parseLong(mqMessage.getBusinessKey1());
        //课程静态化 存储到minio
        generateCourseHtml(mqMessage,courseId);

        //课程索引


        //存储到redis

        return true;
    }

    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        log.debug("开始进行课程静态化，课程id:{}",courseId);
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //判断该任务是否完成
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne > 0){
            log.debug("当前阶段时静态化课程信息任务以及完成不在处理，任务信息{}",mqMessage);
            return;
        }
        //生成静态文件
        File file = coursePublishService.generateCourseHtml(courseId);
        if(file == null){
            XueChengPlusException.cast("课程静态化异常");
        }

        //将静态文件上传到minIO
        coursePublishService.uploadCourseHtml(courseId,file);
        //保存第一阶段状态 更新字段1
        mqMessageService.completedStageOne(id);
    }
}
