package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * @title CourseCategoryServiceImpl
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/6 23:02
 **/
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    /*
如果输入1   categoryMapper.selectTreeNodes(1); 返回的是id下的所有子节点，子节点之间的继承关系还没确定，所有需要定义
1
1-1
1-1-1
1-1-1-1
首先定义一个 categoryTreeDtos用于存放最终结果
其次定义一个HashMap用于装 key 序号 和  value 结点
遍历结果集合： 如果是根结点的直接子结点 先将其放入结果集合
            从map中获取当前结点的父节点，如果父节点的children 为空 则新建list 把自己加进去
 */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //查询数据库得到的课程分类
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //最终返回的列表
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
        HashMap<String, CourseCategoryTreeDto> mapTemp = new HashMap<>();
        courseCategoryTreeDtos.stream().forEach(item->{
            mapTemp.put(item.getId(),item);
        //只将根节点的下级节点放入list
            if(item.getParentid().equals(id)){
                categoryTreeDtos.add(item);
            }
            CourseCategoryTreeDto courseCategoryTreeDto =
                    mapTemp.get(item.getParentid());
            if(courseCategoryTreeDto!=null){
                if(courseCategoryTreeDto.getChildrenTreeNodes() ==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new
                            ArrayList<CourseCategoryTreeDto>());
                }
                //向节点的下级节点list加入节点
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtos;
    }
}
