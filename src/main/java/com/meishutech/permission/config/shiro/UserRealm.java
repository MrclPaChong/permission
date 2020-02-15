package com.meishutech.permission.config.shiro;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.config.shiro.jwt.JwtToken;
import com.meishutech.permission.mapper.LoginDao;
import com.meishutech.permission.service.PermissionService;
import com.meishutech.permission.util.JedisUtil;
import com.meishutech.permission.util.JwtUtil;
import com.meishutech.permission.util.Result.Constant;
import com.meishutech.permission.util.common.StringUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;


/**
 * 自定义Realm
 * @author dolyw.com
 * @date 2018/8/30 14:10
 */
@Service
public class UserRealm extends AuthorizingRealm {
    private Logger logger = LoggerFactory.getLogger(UserRealm.class);
    private final LoginDao loginDao;
    private final PermissionService permissionService;

    @Autowired
    public UserRealm(LoginDao loginDao ,PermissionService permissionService) {
        this.loginDao = loginDao;
        this.permissionService =permissionService;
    }

    /**
     * 大坑，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof JwtToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    @SuppressWarnings("unchecked")		//	告诉编译器忽略指定的警告：允许出现多个重复名称
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        String username = JwtUtil.getClaim(principalCollection.toString(), Constant.ACCOUNT);
        //查询用户所拥有的权限
        JSONObject userPermission = permissionService.getUserPermission(username);
        logger.info("permission的值为:" + userPermission);
        logger.info("本用户权限为:" + userPermission.get("permissionList"));
        simpleAuthorizationInfo.addStringPermissions((Collection<String>) userPermission.get("permissionList"));
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("=====================shiro：UserRealm进入认证============================");
        String token = (String) authenticationToken.getCredentials();
        // 解密获得account，用于和数据库进行对比
        String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
        // 帐号为空
        if (StringUtil.isBlank(account)) {
            throw new AuthenticationException("Token中帐号为空(The account in Token is empty.)");
        }
        // 根据用户姓名查询用户是否存在
        JSONObject dbUser = loginDao.getUserByName(account);
        if (dbUser == null) {
            throw new AuthenticationException("该帐号不存在(The account does not exist.)");
        }
        // 开始认证，要AccessToken认证通过，且Redis中存在RefreshToken，且两个Token时间戳一致
        if (JwtUtil.verify(token) && JedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
            // 获取RefreshToken的时间戳
            String currentTimeMillisRedis = JedisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account).toString();
            // 获取AccessToken时间戳，与RefreshToken的时间戳对比
            if (JwtUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
                return new SimpleAuthenticationInfo(token, token, "userRealm");
            }
        }
        throw new AuthenticationException("Token已过期(Token expired or incorrect.)");
    }
}
