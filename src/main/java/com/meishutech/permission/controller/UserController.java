package com.meishutech.permission.controller;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.service.UserService;
import com.meishutech.permission.util.Result.CommonUtil;
import com.meishutech.permission.util.tree.MenuTreeUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author: cl
 * @description: 用户/角色/权限相关controller
 * @date: 2019/09/23 10:19
 */
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;


	@PostMapping("/getAllUsers")
	public JSONObject getAllUsers(@RequestBody JSONObject jsonObject) {
		return userService.getAllUsers(jsonObject);
	}

	/**
	 * 查询所有用户:前端选择部门列表 :分页
	 */
	@PostMapping("/getAllUser")
	public JSONObject getAllUser(@RequestBody JSONObject jsonObject) {
		return userService.getAllUser(jsonObject);
	}

	/**
	 * 查询用户列表
	 */
	@RequiresPermissions("user:list")
	@GetMapping("/list")
	public JSONObject listUser(HttpServletRequest request) {
		return userService.listUser(CommonUtil.request2Json(request));
	}

	/**
	 *	根据用户ID查询部门/角色ID
	 */
	@PostMapping("/DepartmentIdRoleByUserId")
	public JSONObject DepartmentIdRoleByUserId(@RequestBody JSONObject jsonObject){
		CommonUtil.hasAllRequired(jsonObject, "userId");
		return userService.DepartmentIdRoleIdByUserId(jsonObject);
	}
	/**
	 * 根据部门ID:查询所属部门的用户信息
	 */
	@PostMapping("/userBydepartmentId")
	public JSONObject userBydepartmentId(@RequestBody JSONObject JSONObject) {
		CommonUtil.hasAllRequired(JSONObject, "departmentId");
		return userService.userBydepartmentId(JSONObject);
	}
	/**
	 * 根据用户ID查询用户所有数据：角色/部门/菜单
	 */
	@PostMapping("/RoleDepartmentPermissionByUserId")
	public JSONObject RoleDepartmentPermissionByUserId(@RequestBody JSONObject JSONObject) {
		return null;
	}
	/**
	 *	添加用户  /同时为用户添加部门
	 */
	@RequiresPermissions("user:add")
	@PostMapping("/addUser")
	public JSONObject addUser(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "userName, password, nickName, tel, email, shenFen");
		return userService.addUser(requestJson);
	}

	/**
	 *	修改用户状态 1正常 2异常 deleteStatus
	 */
	@RequiresPermissions("user:update")
	@PostMapping("/updateUser")
	public JSONObject updateUser(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "userId,deleteStatus");
		return userService.updateUser(requestJson);
	}
	/**
	 * 修改用户信息/部门/角色/权限(菜单)
	 */
	@RequiresPermissions("user:update")
	@PostMapping("/updateUsers")
	public JSONObject updateUsers(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "userId");
		return userService.updateUsers(requestJson);
	}

	@PostMapping("/updateUserRole")
	public JSONObject updateUserRole(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "userId,roleId");
		return userService.updateUserRole(requestJson);
	}
	/**
	 *	修改用户直接权限
	 */
	@PostMapping("/updateUserPermission")
	public JSONObject updateUserPermission(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "userId,permissionId");
		return userService.updateUserPermission(requestJson);
	}
	/**
	 * 添加用户直接权限
	 */
	@PostMapping("/addUserPermission")
	public JSONObject addUserPermission(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "userId,permissionId");
		return userService.addUserPermission(requestJson);
	}

	/**
	 * 根据用户ID查询权限ID
	 */
	@PostMapping("/getPermissionByUserId")
	public JSONObject getPermissionByUserId(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "userId");
		return userService.getPermissionByUserId(requestJson);
	}
	/**
	 *	软删除：根据userId修改delete状态为2(1:正常、2删除)
	 */
	@RequiresPermissions("user:update")
	@PostMapping("/delUser")
	public JSONObject delUser(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "userId");
		return userService.delUser(requestJson);
	}


	@RequiresPermissions(value = {"user:add", "user:update"}, logical = Logical.OR)
	@GetMapping("/getAllRoles")
	public JSONObject getAllRoles() {
		return userService.getAllRoles();
	}


	/**
	 * 查询所有角色：前端展示角色列表 :分页
	 */
	@PostMapping("/listRoleAll")
	public JSONObject listRoleAll(@RequestBody JSONObject jsonObject){

		return userService.listRoleAll(jsonObject);
	}
	/**
	 * 角色对应菜单列表及权限
	 */
	@RequiresPermissions("role:list")
	@GetMapping("/listRole")
	public JSONObject listRole() {
		return userService.listRole();
	}

	/**
	 * 角色树形列表：map
	 */
	@PostMapping("/treeMenuRoleMap")
	public JSONObject treeMenuRoleMap(@RequestBody JSONObject request){
		MenuTreeUtil menuTreeUtil = new MenuTreeUtil();
		List<Object> jsonObject = menuTreeUtil.treeMenuRoleMap(null,0,request);
		return CommonUtil.successJson(jsonObject);
	}

	/**
	 *  添加角色
	 */
	@RequiresPermissions("role:add")
	@PostMapping("/addNewRole")
	public JSONObject addNewRole(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "roleName");
		return userService.addNewRole(requestJson);
	}
	/**
	 * 修改角色信息
	 */
	@PostMapping("/updateNewRole")
	public JSONObject updateNewRole(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "roleId");
		return userService.updateNewRole(requestJson);
	}
	/**
	 * 删除角色：软删除 根据delete(1正常 2删除,前台不显示)
	 */
	@PostMapping("/delNewRole")
	public JSONObject delNewRole(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "roleId");
		return userService.delNewRole(requestJson);
	}
	/**
	 * 修改角色状态：根据delete_status(1正常 2异常)
	 */
	@PostMapping("/updateStatusByRoleId")
	public JSONObject updateStatusByRoleId(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "roleId,deleteStatus");
		return userService.updateStatusByRoleId(requestJson);
	}

	/**
	 * 查询所有权限, 给角色分配权限时调用
	 */
	@RequiresPermissions("role:list")
	@GetMapping("/listAllPermission")
	public JSONObject listAllPermission() {
		return userService.listAllPermission();
	}

	/**
	 * 为角色添加菜单权限
	 */
	@RequiresPermissions("role:add")
	@PostMapping("/addRoleMenu")
	public JSONObject addRoleMenu(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "roleId,permissionId");
		return userService.addRoleMenu(requestJson);
	}

	/**
	 * 修改角色权限
	 */
	@RequiresPermissions("role:update")
	@PostMapping("/updateRolePermission")
	public JSONObject updateRole(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "roleId,permissionId");
		return userService.updateRolePermission(requestJson);
	}
	/**
	 * 根据角色Id查询权限Id
	 */
	@PostMapping("/permissionIdByRoleId")
	public JSONObject permissionIdByRoleId(@RequestBody JSONObject jsonObject){
		CommonUtil.hasAllRequired(jsonObject, "roleId");
		return userService.permissionIdByRoleId(jsonObject);
	}
	/**
	 * 删除角色
	 */
	@RequiresPermissions("role:delete")
	@PostMapping("/deleteRole")
	public JSONObject deleteRole(@RequestBody JSONObject requestJson) {
		CommonUtil.hasAllRequired(requestJson, "roleId");
		return userService.deleteRole(requestJson);
	}

	/**
	 * 部门列表
	 */
	@RequiresPermissions("department:list")
	@GetMapping("/listDepartment")
	public JSONObject listDepartment() {
		return userService.listDepartment();
	}

	/**
	 * 单纯部门列表：前端选择部门列表
	 */
	@PostMapping("/getAllDepartment")
	public JSONObject getAllDepartment(@RequestBody JSONObject jsonObject) {
		return userService.getAllDepartment(jsonObject);
	}

	/*	*//**
	 *	树形部门数据 :list形式
	 *//*
	@GetMapping("/treeMenuDepartment1")
	public List<MenuDepartment> tree() {
		return userService.treeMenuDepartment();
	}*/

	/**
	 *	树形部门列表：map形式
	 */
	@RequiresPermissions("department:list")
	@PostMapping("/treeMenuDepartmentMap")
	public JSONObject treeMenuDepartmentMap(@RequestBody JSONObject jsonObject){

		return userService.treeMenuDepartment(jsonObject);
	}

	/**
	 * 删除部门 delete
	 */
	@PostMapping("/deleteByDepartmentId")
	public JSONObject deleteByDepartmentId(@RequestBody JSONObject jsonObject){
		String a =jsonObject.getString("departmentId");
		CommonUtil.hasAllRequired(jsonObject, "departmentId");
		return userService.deleteByDepartmentId(jsonObject);
	}
	/**
	 * 修改状态	delete_status 修改：2 软删除
	 */
	@PostMapping("/deleteStatusByDepartmentId")
	public JSONObject deleteStatusByDepartmentId(@RequestBody JSONObject jsonObject){
		CommonUtil.hasAllRequired(jsonObject, "departmentId,deleteStatus");
		return userService.deleteStatusByDepartmentId(jsonObject);
	}

	/**
	 * 新增部门
	 */
	@PostMapping("/addNewDepartment")
	public JSONObject addNewDepartment(@RequestBody JSONObject jsonObject){
		CommonUtil.hasAllRequired(jsonObject, "departmentName,deleteStatus");
		return userService.addNewDepartment(jsonObject);
	}
	/**
	 * 为部门选取权限(菜单)
	 */
	@PostMapping("/addNewPermissionByDepartmentId")
	public JSONObject addNewPermissionByDepartmentId(@RequestBody JSONObject jsonObject){
		CommonUtil.hasAllRequired(jsonObject, "departmentId,permissionId");
		return userService.addNewPermissionByDepartmentId(jsonObject);
	}
	/**
	 * 根据部门Id获取权限id
	 */
	@PostMapping("/permissionIdByDepartmentId")
	public JSONObject permissionIdByDepartmentId(@RequestBody JSONObject jsonObject){
		CommonUtil.hasAllRequired(jsonObject, "departmentId");
		return userService.permissionIdByDepartmentId(jsonObject);
	}
	/**
	 * 修改部门
	 */
	@PostMapping("/updateDepartment")
	public JSONObject updateDepartment(@RequestBody JSONObject jsonObject){
		CommonUtil.hasAllRequired(jsonObject, "departmentId");
		return userService.updateDepartment(jsonObject);
	}
	/**
	 * 修改部门权限(菜单)
	 */
	@PostMapping("/updateDepartmentPermission")
	public JSONObject updateDepartmentPermission(@RequestBody JSONObject jsonObject){
		System.out.println("*****************"+jsonObject.getInteger("departmentId"));
		System.out.println("*****************"+jsonObject.getString("permissionId"));

		CommonUtil.hasAllRequired(jsonObject, "departmentId,permissionId");
		return userService.updateDepartmentPermission(jsonObject);
	}

	/**
	 *  菜单权限列表：map形式
	 */
	@PostMapping("/treeMenuPermission")
	public JSONObject treeMenuPermission(@RequestBody JSONObject request){
		MenuTreeUtil menuTreeUtil = new MenuTreeUtil();
		List<Object> jsonObject = menuTreeUtil.treeMenuPermission(null,0,request);

		return CommonUtil.successJson(jsonObject);
	}



	/**
	 * 查询所有菜单：前端展示用
	 */
	@PostMapping("/getAllPermission")
	public JSONObject getAllPermission(@RequestBody JSONObject jsonObject){
		return userService.getAllPermission(jsonObject);
	}
	/**
	 * 删除菜单
	 */
	@PostMapping("/delNewPermission")
	public JSONObject delNewPermission(@RequestBody JSONObject jsonObject){
		//效验必填参数前端是否提供
		CommonUtil.hasAllRequired(jsonObject, "permissionId");
		return userService.delNewPermission(jsonObject);
	}
	/**
	 * 修改菜单状态
	 */
	@PostMapping("/updateNewPermissionStatusById")
	public JSONObject updateNewPermissionStatusById(@RequestBody JSONObject jsonObject){
		//效验必填参数前端是否提供
		CommonUtil.hasAllRequired(jsonObject, "permissionId,deleteStatus");
		return userService.updateNewPermissionStatusById(jsonObject);
	}
	/**
	 * 修改菜单
	 */
	@PostMapping("/updateNewPermission")
	public JSONObject updateNewPermission(@RequestBody JSONObject jsonObject){
		//效验必填参数前端是否提供
		CommonUtil.hasAllRequired(jsonObject, "permissionId");
		return userService.updateNewPermission(jsonObject);
	}
	/**
	 * 新增菜单
	 */
	@PostMapping("/addNewPermission")
	public JSONObject addNewPermission(@RequestBody JSONObject jsonObject){
		//效验必填参数前端是否提供
		CommonUtil.hasAllRequired(jsonObject, "menuPermissionCode,menuPermissionName,permissionCode,permissionName,component");
		return userService.addNewPermission(jsonObject);
	}
}
