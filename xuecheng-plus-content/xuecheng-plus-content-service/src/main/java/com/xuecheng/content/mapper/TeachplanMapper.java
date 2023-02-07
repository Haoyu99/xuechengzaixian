package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /**
     * 通过课程id 查看课程的树形结构的计划
     * @author haoyu99
     * @date 2023/2/7 16:31
     * @param courseId
     * @return List<TeachplanDto>
     */

    public List<TeachplanDto> selectTreeNodes(long courseId);

}
