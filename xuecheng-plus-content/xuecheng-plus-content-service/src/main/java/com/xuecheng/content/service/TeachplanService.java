package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/***
 * @title TeachplanService
 * @description 查询课程计划树型结构
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/7 17:12
 **/
public interface TeachplanService {
    public List<TeachplanDto> findTeachplayTree(long courseId);

}
