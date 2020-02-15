package com.meishutech.permission.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.mapper.LoginDao;
import com.meishutech.permission.model.entity.User;
import com.meishutech.permission.service.LoginService;
import com.meishutech.permission.service.PermissionService;
import com.meishutech.permission.util.JwtUtil;
import com.meishutech.permission.util.Result.CommonUtil;
import com.meishutech.permission.util.Result.Constant;
import com.meishutech.permission.util.Result.Constants;
import org.apache.shiro.SecurityUtils;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 登陆ServiceImpl
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginDao loginDao;

	@Autowired
	private PermissionService permissionService;


	/**
	 * 登录表单提交
	 */
	@Override
	public JSONObject authLogin(JSONObject jsonObject) {

		return null;
	}
	/**
	 * 根据用户名和密码查询对应的用户
	 */
	@Override
	public JSONObject getUser(String username, String password) {
		return loginDao.getUser(username, password);
	}

	/**
	 * 根据用户名查询对应的用户
	 */
	@Override
	public JSONObject getUserByName(String account) {
		return loginDao.getUserByName(account);
	}


	/**
	 * 查询当前登录用户的权限等信息
	 */
	@Override
	public JSONObject getInfo() {
		//从token中获取用户账号
		String token = SecurityUtils.getSubject().getPrincipal().toString();
		// 解密获得Account
		String username = JwtUtil.getClaim(token, Constant.ACCOUNT);
		JSONObject info = new JSONObject();
		JSONObject userPermission = permissionService.getUserPermission(username);
		info.put("userPermission", userPermission);
		return CommonUtil.successJson(info);
	}

	/**
	 * 退出登录
	 */
	@Override
	public JSONObject logout() {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			System.out.println(currentUser+"sssssssssss");
			currentUser.logout();
		} catch (Exception e) {
		}
		return CommonUtil.successJson();
	}
}
