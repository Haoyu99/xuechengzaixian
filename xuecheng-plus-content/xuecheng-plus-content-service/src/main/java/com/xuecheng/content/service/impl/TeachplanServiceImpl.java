package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
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

    @Override
    @Transactional
    public void deleteTeachplan(Long id) {
        // 首先根据id查询表 获取计划实例
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan != null){
            //判断是否为一级结点 如果为一级结点则判断是否有子节点
            Integer grade = teachplan.getGrade();
            if(grade == 1){
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid,id);
                Integer count = teachplanMapper.selectCount(queryWrapper);
                //没有子目录
                if(count == 0){
                    teachplanMapper.deleteById(id);
                }else {
                    XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
                }
            }else if(grade == 2){
                //如果是子节点 删除 且删除对应的 TeachplanMedia 表中信息
                //先删除媒体信息
                LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TeachplanMedia::getTeachplanId,id);
                teachplanMediaMapper.delete(queryWrapper);
                teachplanMapper.deleteById(id);

            }
        }
    }

    @Transactional
    @Override
    public void moveTeachplan(String moveType, Long id){
        //确保课程 根据课程计划id可以查到
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan != null){
            // 获取当前同父同级别的总数
            int count = getTeachplanCount(teachplan.getCourseId(), teachplan.getParentid());
            Integer order = teachplan.getOrderby();
            //如果 当前的order是 1 提示 已经是最顶端了
            if(moveType.equals("moveup")){
                if(order == 1){
                    XueChengPlusException.cast("已经处于最顶部");
                }
                //获取上一个课程计划实例
                LambdaQueryWrapper<Teachplan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
                lambdaQueryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
                lambdaQueryWrapper.eq(Teachplan::getOrderby,order - 1);
                Teachplan teachplanUp = teachplanMapper.selectOne(lambdaQueryWrapper);
                if(teachplanUp != null){
                    teachplanUp.setOrderby(order);
                    teachplan.setOrderby(order - 1);
                    teachplanMapper.updateById(teachplanUp);
                    teachplanMapper.updateById(teachplan);
                }


            }else if(moveType.equals("movedown")){
                if(order == count){
                    XueChengPlusException.cast("已经处于最底部");
                }
                // 获取下一个课程实例
                //获取上一个课程计划实例
                LambdaQueryWrapper<Teachplan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
                lambdaQueryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
                lambdaQueryWrapper.eq(Teachplan::getOrderby,order + 1);
                Teachplan teachplanDown = teachplanMapper.selectOne(lambdaQueryWrapper);
                if(teachplanDown != null){
                    teachplanDown.setOrderby(order);
                    teachplan.setOrderby(order + 1);
                    teachplanMapper.updateById(teachplanDown);
                    teachplanMapper.updateById(teachplan);
                }
            }
        }

    }


    /**
    * 取出同等级下的结点总数
    * @author haoyu99
    * @date 2023/2/21 18:02
    * @param courseId
    * @param parentId
    * @return int
    */

    private int getTeachplanCount(long courseId,long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }


    @Override
    @Transactional
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //获取教学计划的id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan == null){
            XueChengPlusException.cast("教学计划不存在");
        }
        //约束校验
        //只有二级目录才可以绑定课程,教学计划不存在无法绑定
        Integer grade = teachplan.getGrade();
        if(grade != 2){
            XueChengPlusException.cast("只有二级目录才可以绑定");
        }
        //删除原来绑定关系
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId);
        teachplanMediaMapper.delete(queryWrapper);
        //添加新纪录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMedia.setCourseId(teachplan.getCourseId());
        //缺少创建人
        teachplanMedia.setCreatePeople("haoyu99");
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public void delAssociationMedia(String mediaId) {
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getMediaId,mediaId));
    }
}
