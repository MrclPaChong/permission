package com.meishutech.permission.mapper;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.model.tree.MenuDepartment;
import com.meishutech.permission.model.tree.MenuPermission;
import com.meishutech.permission.model.tree.MenuRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * 用户DAO
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
public interface UserDao {


	/**
	 * 查询所有用户及角色部门
	 */
	List<JSONObject> getAllUsers(JSONObject jsonObject);
	/**
	 * 查询用户的角色
	 */
	List<JSONObject> getUserRoles();

	/**
	 * 查询用户的部门
	 */
	List<JSONObject> getUserDepartments();


	/**  ****************************** 用户部分   ******************************/
	/**
	 * 查询所有用户:前端选择部门列表
	 */
	List<JSONObject> getAllUser(JSONObject jsonObject);
	/**
	 * 查询用户数量
	 */
	int countUser(JSONObject jsonObject);

	/**
	 * 根据用户ID查询所属部门/角色ID
	 */
	List<JSONObject> DepartmentIdByUserId(JSONObject jsonObject);
	/**
	 * 根据用户ID查询所属部门/角色ID
	 */
	List<JSONObject> RoleIdByUserId(JSONObject jsonObject);
/*	*//**
	 * 根据用户ID查询所属部门/角色
	 *//*
	List<JSONObject> DepartmentRoleByUserId(JSONObject jsonObject);*/

	/**
	 *  根据部门ID：查询所属部门的用户信息
	 */
	List<JSONObject> userBydepartmentId(JSONObject jsonObject);
	int userBydepartmentIdCount(JSONObject jsonObject);
	/**
	 * 查询用户列表
	 */
	List<JSONObject> listUser(JSONObject jsonObject);

	/**
	 * 校验用户名是否已存在
	 */
	int queryExistUsername(JSONObject jsonObject);

	List<JSONObject> queryExistUserRoleDepartment(String username);
	/**
	 * 新增用户
	 */
	int addUser(JSONObject jsonObject);
	//根据新增的人名查询数据库生成的userId
	int getUserId(String userName);

	/**
	 * 删除用户
	 */
	int delUser(@Param("userId") List<Integer> userId);

	/**
	 * 修改用户 、部门
	 */
	int updateUser(JSONObject jsonObject);
	//移除用户旧的部门
	int deleteOldUserDepartment(@Param("userId") Integer userId, @Param("departmentId") List<Integer> departmentId);
	//添加新的部门
	//批量新增用户选取：选择部门
	int addUserDepartmentId(@Param("userId") Integer userId, @Param("departmentId") List<Integer> departmentId);
	//查询出用户部门关系表信息
	Set<Integer> getUserDepartmentId(JSONObject jsonObject);
	//根据前端获取的departmentId修改delete为1
	int updateDeleteByDepartmentId(@Param("departmentId") List<Integer> departmentId);
	//如果前台接收部门为null
	int removeOldDepartmentAll(@Param("userId") Integer userId);

	/**
	 *	修改用户角色
	 */
	//移除用户旧的角色
	int deleteOldUserRole(@Param("userId") Integer userId, @Param("roleId") List<Integer> roleId);
	//添加新的角色
	int addNewUserRole(@Param("userId") Integer userId, @Param("roleId") List<Integer> roleId);
	//查询出用户角色关系表信息
	Set<Integer> getUserRole(JSONObject jsonObject);
	//根据前端获取的roleId修改delete为1
	int updateDeleteByRoleId(@Param("roleId") List<Integer> roleId);
	//如果前台接收角色为null
	int removeOldRoleAll(@Param("userId") Integer userId);

	/**
	 *	修改用户权限(菜单)
	 */
	//移除旧的用户直接的权限-- sys_user_permission
	int deleteOldUserPermission(@Param("userId") Integer userId, @Param("permissionId") List<Integer> permissionId);
	/**
	 * 	添加新的用户直接的权限-- sys_user_permission
	 */
	int addUserPermission(@Param("upid") String upid, @Param("userId") Integer userId, @Param("permissionId") List<Integer> permissionId);
	//查询出用户权限关系表信息
	Set<Integer> getpermissionIdByUserId(JSONObject jsonObject);
	//根据前端获取的userId修改delete_status为1
	int updateUserPermissionByUserId(@Param("permissionId") List<Integer> permissionId);
	//如果前台接收用户直接权限为null
	int removeOldUserPermissionAll(@Param("userId") Integer userId);

	/**
	 * 根据用户ID查询权限ID
	 */
	List<JSONObject> getPermissionByUserId(JSONObject jsonObject);

	/**  ****************************** 角色部分   ******************************/

	/**
	 * 查询所有角色：分页+模糊
	 */
	List<JSONObject> listRoleAll(JSONObject jsonObject);

	/**
	 * 统计角色条数：分页
	 */
	int countRole(JSONObject jsonObject);

	/**
	 *	添加角色：
	 */
	int addNewRole(JSONObject jsonObject);
	/**
	 * 	删除角色：删除角色：软删除 根据delete(1正常 2删除,前台不显示)
	 */
	int delNewRole(JSONObject jsonObject);
	/**
	 * 修改角色状态：根据delete_status(1正常 2异常)
	 */
	int updateStatusByRoleId(@Param("deleteStatus") String deleteStatus, @Param("roleId") List<Integer> roleId);

	/**
	 * 角色列表:对应用户及用户权限
	 */
	List<JSONObject> listRole();

	/**
	 * 查询所有的角色
	 * 在添加/修改用户的时候要使用此方法
	 */
	List<JSONObject> getAllRoles();

	/**
	 * 查询所有权限, 给角色分配权限时调用
	 */
	List<JSONObject> listAllPermission();

	/**
	 * 查询角色：树形列表：map
	 */
	List<MenuRole> listMenuRole(JSONObject jsonObject);

	/**
	 * 查询某角色的全部数据
	 * 在删除和修改角色时调用
	 */
	JSONObject getRoleAllInfo(JSONObject jsonObject);

	/**
	 * 删除角色
	 */
	int removeRole(JSONObject jsonObject);

	/**
	 * 删除本角色全部权限
	 */
	int removeRoleAllPermission(JSONObject jsonObject);

	/**
	 * 修改角色：
	 */
	int updateNewRole(JSONObject jsonObject);
	//移除角色权限
	int removeOldPermission(@Param("roleId") Integer roleId, @Param("permissionId") List<Integer> permissionId);
	//新增角色权限
	int insertRolePermission(@Param("drpid") String drpid, @Param("roleId") Integer roleId, @Param("permissionId") List<Integer> permissionId);
	//查出角色权限关系信息
	Set<Integer> getPermissionIdByRoleId(JSONObject jsonObject);
	//根据前端获取的roleId修改delete_status为1
	//  updateDeleteByDepartmentId2
	//如果前台接收权限为null
	int removeOldPermissionAll(@Param("roleId") Integer roleId);


	/**
	 * 根据角色Id查询权限Id
	 */
	List<JSONObject> permissionIdByRoleId(JSONObject jsonObject);


	/**  ****************************** 部门部分   ******************************/

	/**
	 * 部门列表
	 */
	List<JSONObject> listDepartment();

	/**
	 * 单纯部门列表：前端选择部门列表
	 */
	List<JSONObject> getAllDepartment(JSONObject jsonObject);
	/**
	 * 查询部门总条数
	 */
	int countDepartment(JSONObject jsonObject);

	/**
	 * 新增部门
	 */
	int addNewDepartment(JSONObject jsonObject);
	/**
	 * 新增之前效验部门是否存在
	 */
	int queryExistDepartmentUsername(JSONObject jsonObject);

	/**
	 *	修改部门菜单
	 */
	//移除部门权限
	int  deleteOldPermissionByDepartment(@Param("departmentId") Integer departmentId, @Param("permissionId") List<Integer> permissionId);
	//为部门添加权限
	int addNewPermissionByDepartmentId(@Param("drpid") String drpid, @Param("departmentId") Integer departmentId, @Param("permissionId") List<Integer> permissionId);
	//查询出部门权限关系表信息
	Set<Integer> getDepartmentId(JSONObject jsonObject);
	//根据前端获取的departmentId修改delete为1
	int updateDeleteByDepartmentId2(@Param("permissionId") List<Integer> permissionId);
	//修改部门信息
	int updateDepartment(JSONObject jsonObject);

	//如果permissionId
	int  deleteOldPermissionAllByDepartment(@Param("departmentId") Integer departmentId);


	/**
	 * 删除部门 delete
	 */
	int deleteByDepartmentId(JSONObject jsonObject);
	/**
	 * 修改状态	delete_status 修改：2 软删除
	 */
	int deleteStatusByDepartmentId(@Param("deleteStatus") String deleteStatus, @Param("departmentId") List<Integer> departmentId);

	/**
	 * 部门树形结构数据:map
	 */
	List<MenuDepartment> treeMenuDepartment(JSONObject jsonObject);
	/**
	 * 根据部门Id获取权限id
	 */
	List<JSONObject> permissionIdByDepartmentId(JSONObject jsonObject);



	/**  ****************************** 菜单部分   ******************************/
	/**
	 * 菜单树形列表：map
	 */
	List<MenuPermission> treeMenuPermission(JSONObject jsonObject);

	/**
	 * 查询所有菜单：前端展示用
	 */
	List<JSONObject> getAllPermission(JSONObject jsonObject);
	/**
	 * 新增菜单
	 */
	int addNewPermission(JSONObject jsonObject);
	//效验菜单是否已存在
    String queryExistmenuName(@Param("menuPermissionName") String menuPermissionName);
	// 根据parentId 查询出 grade：等级  在新增时为子集添加grade时使用原来的等级+1：新等级
    List<String> gradeByPermissionParentId(@Param("parentId") String parentId);
	/**
	 * 删除菜单
	 */
	int delNewPermission(@Param("permissionId") List<Integer> permissionId);
	/**
	 * 删除菜单对应的数据:sys_department_role_permission表
	 */
	int delNewPermissionToDepartmentRolePermission(@Param("permissionId") List<Integer> permissionId);
	/**
	 * 修改菜单状态
	 */
	int updateNewPermissionStatusById(@Param("deleteStatus") String deleteStatus, @Param("permissionId") List<Integer> departmentId);

	/**
	 * 修改菜单
	 */
	int updateNewPermission(JSONObject jsonObject);

	/**
	 *统计权限数据总条数
	 */
	int countPermission(JSONObject jsonObject);
}
