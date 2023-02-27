package com.xuecheng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/***
 * @title FreemarkerController
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/27 9:24
 **/
@Controller //因为freemarker返回页面而不是json
public class FreemarkerController {
    @GetMapping("/testfreemarker")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        //设置模型数据
        modelAndView.addObject("name","小明");
        //设置模板名称，就是模板文件的名称去掉扩展名
        modelAndView.setViewName("test");
        return modelAndView;
    }

}
