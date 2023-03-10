package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {
   /**
    * 根据分片参数获取待处理任务
    * @author haoyu99
    * @date 2023/2/10 18:36
    * @param shardTotal 分片总数
    * @param shardIndex 分片序号
    * @param count 任务数
    * @return List<MediaProcess>
    */

    @Select("SELECT * FROM media_process t WHERE t.id % #{shardTotal} = #{shardIndex} and t.status='1' limit #{count}")
    List<MediaProcess> selectListByShardIndex
            (@Param("shardTotal") int shardTotal, @Param("shardIndex")
            int shardIndex, @Param("count") int count);}
