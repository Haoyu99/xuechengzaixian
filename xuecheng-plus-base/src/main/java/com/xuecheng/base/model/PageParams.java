package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

/***
 * @title PageParams
 * @description 分页查询通用参数
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/3 16:50
 **/
@Data
@ToString
public class PageParams {
    //当前页码默认值
    public static final long DEFAULT_PAGE_CURRENT = 1L;
    //每页记录数默认值
    public static final long DEFAULT_PAGE_SIZE = 10L;
    //当前页码

    private Long pageNo = DEFAULT_PAGE_CURRENT;
    //每页记录数默认值
    private Long pageSize = DEFAULT_PAGE_SIZE;
    public PageParams(){
    }
    public PageParams(long pageNo,long pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
