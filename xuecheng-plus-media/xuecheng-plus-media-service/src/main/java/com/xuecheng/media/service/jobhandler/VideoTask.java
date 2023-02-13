package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class VideoTask {
    private static Logger logger = LoggerFactory.getLogger(VideoTask.class);
    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;
    //ffmpeg绝对路径

    @Value("${videoprocess.ffmpegpath}")
    String ffmpegPath;



    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {

        // 分片参数
        // 分配序号
        int shardIndex = XxlJobHelper.getShardIndex();
        // 分片总数
        int shardTotal = XxlJobHelper.getShardTotal();

        // 查询待处理任务  ,一次处理的任务数和cpu核心一样多
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardTotal, shardIndex, 2);

        if(mediaProcessList == null || mediaProcessList.size() <= 0 ){
            log.debug("查询到的待处理任务为0");
            return;
        }
        int size = mediaProcessList.size();
        // 启动多线程 (创建size个线程数量的线程池)
        ExecutorService threadPool = Executors.newFixedThreadPool(size);

        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        //遍历mediaProcessList 讲任务放入线程池
        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(() ->{
                // 任务的执行逻辑
                    // 视频处理的状态
                String status = mediaProcess.getStatus();
                if("2".equals(status)){
                    log.debug("视频已经处理，视频信息{}",mediaProcess);
                    countDownLatch.countDown();//异常计数器也要减一
                    return;
                }
                //桶
                String bucket = mediaProcess.getBucket();
                //存储路径
                String filePath = mediaProcess.getFilePath();
                //原始视频的md5值
                String fileId = mediaProcess.getFileId();
                //原始文件名称
                String filename = mediaProcess.getFilename();
                // 创建下载临时文件
                File originalVideo = null;
                //处理结束的mp4文件
                File mp4Video = null;
                try {
                    originalVideo = File.createTempFile("original", null);
                    mp4Video = File.createTempFile("mp4", ".mp4");
                } catch (IOException e) {
                    countDownLatch.countDown();//异常计数器也要减一
                    log.error("下载待处理的原始文件前创建临时文件失败");
                    return;
                }
                //将要处理的视频下载到本地
                try {
                    mediaFileService.downloadFileFromMinIO(originalVideo, bucket, filePath);
                } catch (Exception e) {
                    countDownLatch.countDown();//异常计数器也要减一
                    log.debug("下载原始文件出错:{},文件信息{}",e.getMessage(),mediaProcess);
                    return;
                }
                //调用工具类处理
                Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath,originalVideo.getAbsolutePath(),mp4Video.getName(),mp4Video.getAbsolutePath());
                //开始处理视频
                String result = videoUtil.generateMp4();
                String statusNew = "3";
                String url = null;
                if("success".equals(result)){
                    //转换成功
                    //上传到minio
                    String objectName = getFilePathByMd5(fileId, ".mp4");
                    try {
                        mediaFileService.addMediaFilesToMinIO(mp4Video.getAbsolutePath(),bucket,objectName);
                    } catch (Exception e) {
                        countDownLatch.countDown();//异常计数器也要减一
                        log.debug("上传文件出错{}",e.getMessage());
                        return;
                    }
                    statusNew = "2";
                    url = "/" + bucket + "/" + objectName;
                }
                //记录任务处理的结果
                try {
                    mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(),statusNew,fileId,url,result);
                } catch (Exception e) {
                    countDownLatch.countDown();//异常计数器也要减一
                    log.debug("保存任务结果出错{}",e.getMessage());
                    return;
                }
                //上传成功后删除临时文件
//                mp4Video.delete();

                countDownLatch.countDown();
            });
        });
        //阻塞到任务执行完成,当计数器归零，这里的阻塞结束
        countDownLatch.await(30, TimeUnit.MINUTES);


    }

    /**
     * 通过MD5值得到文件的路径
     * @author haoyu99
     * @date 2023/2/10 10:40
     * @param fileMd5
     * @param fileExt
     * @return String
     */
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }



}
