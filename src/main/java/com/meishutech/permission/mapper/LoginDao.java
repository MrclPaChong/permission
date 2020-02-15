package com.meishutech.permission.mapper;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.model.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 登陆DAO
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
public interface LoginDao {
	/**
	 * 根据用户名和密码查询对应的用户
	 */
	JSONObject getUser(@Param("username") String username, @Param("password") String password);

	/**
	 *根据用户名查用户信息
	 */
	JSONObject getUserByName(@Param("username") String account);

	/**
	 * 查询当前登录用户的权限等信息
	 */
	JSONObject getInfo();
}
