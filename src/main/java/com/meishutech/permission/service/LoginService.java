package com.meishutech.permission.service;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.model.entity.User;

/**
 * 登陆Service
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
public interface LoginService {


	/**
	 * 登录表单提交
	 */
	JSONObject authLogin(JSONObject jsonObject);

	/**
	 * 根据用户名和密码查询对应的用户
	 *
	 * @param username 用户名
	 * @param password 密码
	 */
	JSONObject getUser(String username, String password);

	/**
	 *根据用户名查用户信息
	 */
	JSONObject getUserByName(String account);

	/**
	 * 查询当前登录用户的权限等信息
	 */
	JSONObject getInfo();

	/**
	 * 退出登录
	 */
	JSONObject logout();
}
