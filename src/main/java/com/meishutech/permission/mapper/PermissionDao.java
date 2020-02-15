package com.meishutech.permission.mapper;

import com.alibaba.fastjson.JSONObject;

import java.util.Set;

/**
 * 权限DAO
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
public interface PermissionDao {
	/**
	 * 查询用户的角色 菜单 权限
	 */
	JSONObject getUserPermission(String username);
	/**
	 * 查询用户的角色
	 */
	Set<JSONObject> getUserRole(String username);

	/**
	 * 查询用户的部门
	 */
	Set<JSONObject> getUserDepartment(String username);
	/**
	 * 查询所有的菜单
	 */
	Set<JSONObject> getAllMenu();

	/**
	 * 查询所有的权限
	 */
	Set<String> getAllPermission();

	/**
	 * 	查询匿名/游客/菜单
	 *
	 */
	Set<JSONObject> getAnonymousMenu(String username);
	/**
	 * 	查询匿名/游客/权限：Anonymous(匿名)
	 *
	 */
	Set<String> getAnonymousPermission(String username);
}
