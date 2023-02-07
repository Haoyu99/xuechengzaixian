package com.xuecheng.base.exception;

import java.io.Serializable;

/***
 * @title RestErrorResponse
 * @description 错误信息包装类
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/7 12:07
 **/
public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
