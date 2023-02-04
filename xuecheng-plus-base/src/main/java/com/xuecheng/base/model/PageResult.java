package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/***
 * @title PageResult
 * @description 分页查询结果模型类
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/3 17:10
 **/
@Data
@ToString
public class PageResult<T> implements Serializable {
    // 数据列表
    private List<T> items;
    //总记录数
    private long counts;
    //当前页码
    private long page;
    //每页记录数
    private long pageSize;
    public PageResult(List<T> items, long counts, long page, long pageSize) {
        this.items = items;
        this.counts = counts;
        this.page = page;
        this.pageSize = pageSize;
    }
}
