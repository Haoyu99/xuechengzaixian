package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/***
 * @title TeachplanServiceImpl
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/7 17:13
 **/
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Override
    public List<TeachplanDto> findTeachplayTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto dto) {
        Long id = dto.getId();
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan == null){
            //新增操作
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(dto.getCourseId(),
                    dto.getParentid());
             teachplan = new Teachplan();
            //设置排序号
            teachplan.setOrderby(count+1);
            BeanUtils.copyProperties(dto,teachplan);
            teachplanMapper.insert(teachplan);

        }else {
            //更新操作
            BeanUtils.copyProperties(dto,teachplan);
            teachplanMapper.updateById(teachplan);

        }
    }

    private int getTeachplanCount(long courseId,long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
