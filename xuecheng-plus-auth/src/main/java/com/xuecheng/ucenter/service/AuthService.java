package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/***
 * @title AutoService
 * @description 认证接口
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/3/2 19:29
 **/
public interface AuthService {

    /**
     * 认证方法实现
     * @author haoyu99
     * @date 2023/3/2 19:31
     * @param authParamsDto 认证参数
     * @return XcUser
     */

    XcUserExt execute(AuthParamsDto authParamsDto);
}
