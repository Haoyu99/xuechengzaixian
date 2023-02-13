package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/***
 * @title MediaFileProcessService
 * @description 媒资文件处理业务方法
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/10 18:37
 **/
public interface MediaFileProcessService {
    /**
     * 获取待处理任务
     * @author haoyu99
     * @date 2023/2/10 18:37
     * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count 获取记录数
     * @return List<MediaProcess>
     */

    public List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex,int count);
     /**
      * 保存任务结果
      * @author haoyu99
      * @date 2023/2/10 18:43
      * @param taskId 任务ID
      * @param status 任务状态
      * @param fileId 文件ID
      * @param url 文件URL
      * @param errorMsg 错误信息

      */

    public void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
