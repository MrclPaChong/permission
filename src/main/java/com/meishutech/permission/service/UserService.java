package com.meishutech.permission.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 用户Service
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
public interface UserService {

	/**
	 * 查询所有用户及角色部门
	 */
	JSONObject getAllUsers(JSONObject jsonObject);
	/**
	 * 查询所有用户:前端选择部门列表
	 */
	JSONObject getAllUser(JSONObject jsonObject);
	/**
	 * 用户列表
	 */
	JSONObject listUser(JSONObject jsonObject);

	/**
	 * 根据用户ID查询所属部门ID
	 */
	JSONObject DepartmentIdRoleIdByUserId(JSONObject jsonObject);


	/**
	 *  根据部门ID：数组-->查询所属部门的用户信息
	 */
	JSONObject userBydepartmentId(JSONObject jsonObject);
	/**
	 * 添加用户
	 */
	JSONObject addUser(JSONObject jsonObject);

	/**
	 * 软删除：根据userId修改delete状态为2(1:正常、2删除)
	 */
	JSONObject delUser(JSONObject jsonObject);

	/**
	 *	修改用户状态 1正常 2异常 deleteStatus
	 */
	JSONObject updateUser(JSONObject jsonObject);
	/**
	 * 修改用户信息
	 */
	JSONObject updateUsers(JSONObject jsonObject);

	/**
	 *	修改用户角色
	 */
	JSONObject updateUserRole(JSONObject jsonObject);

	/**
	 *	修改用户直接权限
	 */
	JSONObject updateUserPermission(JSONObject jsonObject);
	/**
	 * 添加用户直接权限
	 */
	JSONObject addUserPermission(JSONObject jsonObject);

	/**
	 * 根据用户ID查询权限ID
	 */
	JSONObject getPermissionByUserId(JSONObject jsonObject);
	/**
	 * 查询所有角色:分页+模糊
	 */
	JSONObject listRoleAll(JSONObject jsonObject);

	/**
	 * 添加角色
	 */
	JSONObject addNewRole(JSONObject jsonObject);
	/**
	 * 	删除角色：删除角色：软删除 根据delete(1正常 2删除,前台不显示)
	 */
	JSONObject delNewRole(JSONObject jsonObject);
	/**
	 * 	修改角色状态 delete_status 1 正常 2异常
	 */
	JSONObject updateStatusByRoleId(JSONObject jsonObject);
	/**
	 * 修改角色："roleId,parentId,roleName,desc,deleteStatus"
	 */
	JSONObject updateNewRole(JSONObject jsonObject);

	/**
	 * 角色列表
	 */
	JSONObject listRole();
	/**
	 * 查询所有的角色
	 * 在添加/修改用户的时候要使用此方法
	 */
	JSONObject getAllRoles();


	/**
	 * 查询所有权限, 给角色分配权限时调用
	 */
	JSONObject listAllPermission();

	/**
	 * 为角色选取菜单及权限
	 */
	JSONObject addRoleMenu(JSONObject jsonObject);

	/**
	 * 修改角色权限
	 */
	JSONObject updateRolePermission(JSONObject jsonObject);

	/**
	 * 根据角色Id查询权限Id
	 */
	JSONObject permissionIdByRoleId(JSONObject jsonObject);

	/**
	 * 删除角色
	 */
	JSONObject deleteRole(JSONObject jsonObject);


	/**
	 * 部门列表
	 */
	JSONObject listDepartment();

	/**
	 * 部门树形列表：map
	 */
	JSONObject treeMenuDepartment(JSONObject jsonObject);

	/**
	 * 单纯部门列表：前端选择部门列表
	 */
	JSONObject getAllDepartment(JSONObject jsonObject);

	/**
	 * 删除部门 delete
	 */
	JSONObject deleteByDepartmentId(JSONObject jsonObject);
	/**
	 * 修改状态	delete_status 修改：2 软删除
	 */
	JSONObject deleteStatusByDepartmentId(JSONObject jsonObject);
	/**
	 * 新增部门
	 */
	JSONObject addNewDepartment(JSONObject jsonObject);
	/**
	 * 为部门选择菜单
	 */
	JSONObject addNewPermissionByDepartmentId(JSONObject jsonObject);
	/**
	 * 修改部门
	 */
	JSONObject updateDepartment(JSONObject jsonObject);
	/**
	 * 修改部门菜单
	 */
	JSONObject updateDepartmentPermission(JSONObject jsonObject);
	/**
	 * 根据部门Id获取权限id
	 */
	JSONObject permissionIdByDepartmentId(JSONObject jsonObject);
	/**
	 * 查询所有菜单
	 */
	JSONObject getAllPermission(JSONObject jsonObject);
	/**
	 * 新增菜单
	 */
	JSONObject addNewPermission(JSONObject jsonObject);
	/**
	 * 删除菜单
	 */
	JSONObject delNewPermission(JSONObject jsonObject);
	/**
	 * 修改菜单状态
	 */
	JSONObject updateNewPermissionStatusById(JSONObject jsonObject);
	/**
	 * 修改菜单
	 */
	JSONObject updateNewPermission(JSONObject jsonObject);

}
