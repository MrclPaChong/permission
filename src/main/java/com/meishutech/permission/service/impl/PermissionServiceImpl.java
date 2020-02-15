package com.meishutech.permission.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.meishutech.permission.mapper.PermissionDao;
import com.meishutech.permission.mapper.UserDao;
import com.meishutech.permission.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 权限ServiceImpl
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PermissionDao permissionDao;

	/**
	 * 查询某用户的 角色  菜单列表   权限列表
	 */
	@Override
	public JSONObject getUserPermission(String username) {
		JSONObject userPermission = getUserPermissionFromDB(username);
		return userPermission;
	}

	/**
	 * 从数据库查询用户权限信息
	 */
	private JSONObject getUserPermissionFromDB(String username) {

		// 1.
		//查询拥有角色/部门的用户权限
		Set<JSONObject> roleList = permissionDao.getUserRole(username);
		Set<JSONObject> departmentList =permissionDao.getUserDepartment(username);
		JSONObject userPermission = permissionDao.getUserPermission(username);
		userPermission.put("roleList",roleList);
		userPermission.put("departmentList",departmentList);
		System.out.println(userPermission+"数据库查出用户权限");
		// 2.
		//管理员角色ID为1
		String adminId ="1";
		//如果是管理员 管理员1，普通用户2，匿名用户3
		String identity = "identity";
		if (adminId .equals(userPermission.getString(identity))) {
			System.out.println(userPermission.getString(identity));
			//查询所有菜单  所有权限
			Set<JSONObject> roleListAdmin = permissionDao.getUserRole(username);
			Set<JSONObject> departmentListAdmin =permissionDao.getUserDepartment(username);
			Set<JSONObject> menuList = permissionDao.getAllMenu();
			Set<String> permissionList = permissionDao.getAllPermission();

			userPermission.put("menuList", menuList);
			System.out.println(menuList+"**********管理员账号菜单权限***********");
			userPermission.put("permissionList", permissionList);
			System.out.println(permissionList+"**********管理员账号菜单权限***********");
			userPermission.put("roleList",roleListAdmin);
			userPermission.put("departmentList",departmentListAdmin);
		}
		// 3.
		//根据username查询属于角色/部门的用户是否存在
		List<JSONObject> listqueryExistUserRoleDepartmentId = userDao.queryExistUserRoleDepartment(username);
		//匿名登陆根据登陆名查询权限菜单
		if (listqueryExistUserRoleDepartmentId.size() == 0||"".equals(listqueryExistUserRoleDepartmentId) || listqueryExistUserRoleDepartmentId.equals(null)){
			Set<JSONObject> roleListAnonymous = permissionDao.getUserRole(username);
			Set<JSONObject> departmentListAnonymous =permissionDao.getUserDepartment(username);
			Set<JSONObject> menuListAnonymous =permissionDao.getAnonymousMenu(username);
			Set<String> permissionListAnonymous =permissionDao.getAnonymousPermission(username);
			userPermission.put("menuList",menuListAnonymous);
			userPermission.put("permissionList",permissionListAnonymous);
			userPermission.put("roleList",roleListAnonymous);
			userPermission.put("departmentList",departmentListAnonymous);

		}
		return userPermission;
	}

}
