package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/***
 * @title MediaFileProcessService
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/10 18:38
 **/
@Slf4j
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;
    
    /** 
     * 分布式处理  获取待处理任务
     * @author haoyu99
     * @date 2023/2/10 18:44
     * @param shardTotal 
     * @param shardIndex 
     * @param count 
     * @return List<MediaProcess> 
     */
    
    @Override
    public List<MediaProcess> getMediaProcessList( int shardTotal,int shardIndex, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal,shardIndex,count);
    }

    /** 
     * 视频转码后（成功或者失败）保存任务结果
     * @author haoyu99
     * @date 2023/2/10 18:44
     * @param taskId 
     * @param status 
     * @param fileId 
     * @param url 
     * @param errorMsg 
     
     */
    @Transactional
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //查询这个任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess == null){
            log.debug("更新任务状态时，此{}任务为空",mediaProcess.getFilename());
            return;
        }
        // 这里为什么要新建一个对象保存
        LambdaQueryWrapper<MediaProcess> lambdaQueryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        if("3".equals(status)){
            //任务失败
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrormsg(errorMsg);
            mediaProcessMapper.update(mediaProcess_u,lambdaQueryWrapperById);
        }
        if("2".equals(status)){
            //成功
            mediaProcess.setStatus("2");
            mediaProcess.setUrl(url);
            mediaProcess.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.updateById(mediaProcess);
        }
        //如果添加成功添加到历史记录表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //从media_process删除
        mediaProcessMapper.deleteById(taskId);

        //media file 表的url还未更新

    }
}
