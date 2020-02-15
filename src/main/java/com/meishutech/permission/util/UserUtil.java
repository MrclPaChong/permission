package com.meishutech.permission.util;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.exception.CustomException;
import com.meishutech.permission.mapper.LoginDao;
import com.meishutech.permission.model.entity.User;
import com.meishutech.permission.util.Result.Constant;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取当前登录用户工具类
 * @author Wang926454
 * @date 2019/3/15 11:45
 */
@Component
public class UserUtil {

    private final LoginDao loginDao;

    @Autowired
    public UserUtil(LoginDao loginDao) {
        this.loginDao = loginDao;
    }

    /**
     * 获取当前登录用户
     * @param
     * @return com.wang.model.UserDto
     * @author Wang926454
     * @date 2019/3/15 11:48
     */
    public JSONObject getUser() {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        // 解密获得Account
        String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
        //根据账号从数据库查询用户信息
        JSONObject dbUser = loginDao.getUserByName(account);
        dbUser.remove("password");
        // 用户是否存在
        if (dbUser == null) {
            throw new CustomException("该帐号不存在(The account does not exist.)");
        }
        return dbUser;
    }

    /**
     * 获取当前登录用户Token
     * @param
     * @return com.wang.model.UserDto
     * @author Wang926454
     * @date 2019/3/15 11:48
     */
    public String getToken() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }

    /**
     * 获取当前登录用户Account
     * @param
     * @return com.wang.model.UserDto
     * @author Wang926454
     * @date 2019/3/15 11:48
     */
    public String getAccount() {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        // 解密获得Account
        return JwtUtil.getClaim(token, Constant.ACCOUNT);
    }
}
