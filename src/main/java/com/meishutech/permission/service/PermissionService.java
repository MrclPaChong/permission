package com.meishutech.permission.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 权限Service
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
public interface PermissionService {
	/**
	 * 查询某用户的 角色  菜单列表   权限列表
	 */
	JSONObject getUserPermission(String username);
}
