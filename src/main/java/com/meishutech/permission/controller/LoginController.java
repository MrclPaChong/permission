package com.meishutech.permission.controller;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.exception.CustomException;
import com.meishutech.permission.exception.CustomUnauthorizedException;
import com.meishutech.permission.service.LoginService;
import com.meishutech.permission.util.AesCipherUtil;
import com.meishutech.permission.util.JedisUtil;
import com.meishutech.permission.util.JwtUtil;
import com.meishutech.permission.util.Result.Constant;
import com.meishutech.permission.util.Result.ResponseBean;
import com.meishutech.permission.util.UserUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author cl
 * @company XX公司
 * @create 2019-10-22 14:51
 */
@RestController
@RequestMapping("/login")
@PropertySource("classpath:config.properties")
public class LoginController {

    /**
     * RefreshToken过期时间
     */
    @Value("${refreshTokenExpireTime}")
    private String refreshTokenExpireTime;

    private final UserUtil userUtil;

    private final LoginService loginService;

    @Autowired
    public LoginController(UserUtil userUtil, LoginService loginService) {
        this.userUtil = userUtil;
        this.loginService = loginService;
    }

    /**
     *  用户登陆
     */
    @PostMapping("/auth")
    public ResponseBean login(@RequestBody JSONObject jsonObject, HttpServletResponse httpServletResponse) {

        //加密密码
        String key11 = AesCipherUtil.enCrypto(jsonObject.getString("username") + jsonObject.getString("password"));
        System.out.println(key11+"====================");

        String account = jsonObject.getString("username");
        // 查询数据库中的帐号信息
        JSONObject userDtoTemp = loginService.getUserByName(account);
        System.out.println(userDtoTemp);
        if (userDtoTemp == null) {
            throw new CustomUnauthorizedException("该帐号不存在(The account does not exist.)");
        }
        if ("2".equals(userDtoTemp.getString("deleteStatus"))){
            throw new CustomUnauthorizedException("该帐号被锁定,请联系管理员(This account is locked, please contact the administrator.)");
        }
        // 密码进行AES解密
        String key = AesCipherUtil.deCrypto(userDtoTemp.getString("password"));
        // 因为密码加密是以帐号+密码的形式进行加密的，所以解密后的对比是帐号+密码
        if (key.equals(jsonObject.getString("username") + jsonObject.getString("password"))) {
            // 清除可能存在的Shiro权限信息缓存
            if (JedisUtil.exists(Constant.PREFIX_SHIRO_CACHE + jsonObject.getString("username"))) {
                JedisUtil.delKey(Constant.PREFIX_SHIRO_CACHE + jsonObject.getString("username"));
            }
            // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            JedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + jsonObject.getString("username"), currentTimeMillis, Integer.parseInt(refreshTokenExpireTime));
            // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
            String token = JwtUtil.sign(jsonObject.getString("username"), currentTimeMillis);
            httpServletResponse.setHeader("Authorization", token);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            return new ResponseBean(HttpStatus.OK.value(), "登录成功(Login Success.)", null);
        } else {
            throw new CustomUnauthorizedException("帐号或密码错误(Account or Password Error.)");
        }
    }



    /**
     * 获取所有在线用户
     */
    @GetMapping("/onlineUser")
    public ResponseBean online() {
        List<Object> userDtos = new ArrayList<Object>();
        // 查询所有Redis键
        Set<String> keys = JedisUtil.keysS(Constant.PREFIX_SHIRO_REFRESH_TOKEN + "*");
        for (String key : keys) {
            if (JedisUtil.exists(key)) {
                // 根据:分割key，获取最后一个字符(帐号)
                String[] strArray = key.split(":");
               JSONObject dbUser = loginService.getUserByName(strArray[strArray.length - 1]);
                // 设置登录时间
                dbUser.put("loginTime",new Date(Long.parseLong(JedisUtil.getObject(key).toString())));
                //删除返回值中的密码
                dbUser.remove("password");
                userDtos.add(dbUser);
            }
        }
        if (userDtos == null || userDtos.size() <= 0) {
            throw new CustomException("查询失败(Query Failure)");
        }
        return new ResponseBean(HttpStatus.OK.value(), "查询成功(Query was successful)", userDtos);
    }

    /**
     * 查询当前登录用户及权限信息
     */
    @PostMapping("/getInfo")
    public JSONObject getInfo() {
        return loginService.getInfo();
    }

    /**
     * 获取当前登录用户
     * //　 @RequiresAuthentication:验证用户是否登录，等同于方法subject.isAuthenticated() 结果为true时。
     */
    @GetMapping("/currentUser")
    @RequiresAuthentication
    public ResponseBean currentUser() {
        // 获取当前登录用户
        JSONObject userDto = userUtil.getUser();
        // 获取当前登录用户Token
        String token = userUtil.getToken();
        // 获取当前登录用户Account
        String account = userUtil.getAccount();
        userDto.put("token",token);
        return new ResponseBean(HttpStatus.OK.value(), "您已经登录了(You are already logged in)", userDto);
    }

    /**
     *  踢出在线用户信息
     */
    @PostMapping("/currentOut")
    public ResponseBean currentOut(JSONObject jsonObject) {
        //根据用户id获取用户姓名
        //JSONObject dbUser = loginService.getUserByName(jsonObject.getString("id"));
        if (JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + jsonObject.getString("username"))) {
            if (JedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + jsonObject.getString("username")) > 0) {
                return new ResponseBean(HttpStatus.OK.value(), "剔除成功(Delete Success)", null);
            }
        }
        throw new CustomException("剔除失败，Account不存在(Deletion Failed. Account does not exist.)");
    }


    /**
     * 退出登陆
     */
    @PostMapping("/logout")
    public ResponseBean loginOut(){
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        // 解密获得Account
        String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
        if (JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
            if (JedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account) > 0) {
                return new ResponseBean(HttpStatus.OK.value(), "退出成功(loginOut Success)", null);
            }
        }
        throw new CustomException("退出失败，Account不存在(Deletion Failed. Account does not exist.)");
    }
}
