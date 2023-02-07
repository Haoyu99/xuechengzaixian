package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/***
 * @title GlobalExceptionHandler
 * @description 全局异常处理器
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/7 12:01
 **/
@Slf4j
@ControllerAdvice //控制器增强
public class GlobalExceptionHandler {
    //处理XueChengPlusException异常  此类异常由程序员主动抛出（可预知异常）
    @ExceptionHandler(XueChengPlusException.class) //此方法捕获XueChengPlusException的异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    @ResponseBody //将信息返回为JSon格式
    public RestErrorResponse customException(XueChengPlusException e){
        log.error("【系统异常】{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());
    }
    //捕获不可预知的异常 Exception
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    public RestErrorResponse exception(Exception e) {
        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

}
