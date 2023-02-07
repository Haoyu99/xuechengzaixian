package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

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
    public RestErrorResponse doCustomException(XueChengPlusException e){
        log.error("【系统异常】{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());
    }
    //捕获不可预知的异常 Exception
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    public RestErrorResponse doException(Exception e) {
        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    public RestErrorResponse doMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuffer errMsg = new StringBuffer();
        //校验的错误信息
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        fieldErrors.forEach(error -> {
            errMsg.append(error.getDefaultMessage()).append(",");
        });
        log.error(errMsg.toString());
        return new RestErrorResponse(errMsg.toString());
    }

}
