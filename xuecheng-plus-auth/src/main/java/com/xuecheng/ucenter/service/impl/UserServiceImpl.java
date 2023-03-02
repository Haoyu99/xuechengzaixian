package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/***
 * @title UserServiceImpl
 * @description UserDetailsService的实现类  通过用户的name 返回详细信息 （通过数据库查询）
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/3/2 15:37
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    XcUserMapper userMapper;
    //传入的是username
    /**
     * 查询用户信息组成用户身份信息
     * @author haoyu99
     * @date 2023/3/2 16:04
     * @param s AuthParamsDto的JSon串
     * @return UserDetails
     */

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;
        try {
            //将认证参数转为AuthParamsDto类型
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}",s);
            throw new RuntimeException("认证请求数据格式不对");
        }
        //获取认证类型
        String authType = authParamsDto.getAuthType();
        //从spring容器中拿具体的认证实例
        AuthService authService = applicationContext.getBean(authType + "_authservice",AuthService.class);

        //开始认证  ,认证成功拿到用户信息
        XcUserExt xcUserExt = authService.execute(authParamsDto);

        // 根据xcUserExt对象构造一个UserDetail对象

//        String username1 = authParamsDto.getUsername();
//        //从数据库查用户信息
//        XcUser user = userMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username1));
//        if(user == null){
//            //账号不存在
//            return null;
//        }
//        // 账号
//        String username = user.getUsername();
//        // 获取正确的密码
//        String password_DB = user.getPassword();
//        // 用户权限
//        String [] authorities = {"p1"};
//        // 密码设置为空
//        user.setPassword(null);
//        String userJson = JSON.toJSONString(user);
//        UserDetails userDetails = User.withUsername(userJson).password(password_DB).authorities(authorities).build();
        return getUserPrincipal(xcUserExt);

    }

    /**
     * 根据xcUserExt对象构造一个UserDetail对象
     * @author haoyu99
     * @date 2023/3/2 19:54
     * @param user
     * @return UserDetails
     */

    public UserDetails getUserPrincipal(XcUserExt user){
        // 用户权限
        String [] authorities = {"p1"};
        // 密码设置为空
        user.setPassword(null);
        String userJson = JSON.toJSONString(user);
        UserDetails userDetails = User.withUsername(userJson).password("").authorities(authorities).build();
        return userDetails;
    }
}
