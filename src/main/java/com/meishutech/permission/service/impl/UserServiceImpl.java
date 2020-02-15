package com.meishutech.permission.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.exception.CustomException;
import com.meishutech.permission.mapper.UserDao;
import com.meishutech.permission.service.UserService;
import com.meishutech.permission.util.AesCipherUtil;
import com.meishutech.permission.util.Result.CommonUtil;
import com.meishutech.permission.util.Result.Constant;
import com.meishutech.permission.util.Result.ErrorEnum;
import com.meishutech.permission.util.tools.StringTools;
import com.meishutech.permission.util.tree.MenuTreeUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户ServiceImpl
 *
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Transactional
    @Override
    public JSONObject getAllUsers(JSONObject jsonObject) {
        CommonUtil.fillPageParam(jsonObject);
        int count = userDao.countUser(jsonObject);
        List<JSONObject> getAllUsers = userDao.getAllUsers(jsonObject);
        List<JSONObject> getUserDepartments = userDao.getUserDepartments();
        System.out.println(getUserDepartments+"部门");
        List<JSONObject> getUserRoles = userDao.getUserRoles();
        System.out.println(getUserRoles+"角色");
        for (JSONObject getAllUser :getAllUsers){
                for (JSONObject getUserDepartment :getUserDepartments){
                    if (getAllUser.getString("userId").equals(getUserDepartment.getString("userId")))
                        getAllUser.put("departmentLists",getUserDepartments);
                }
                for (JSONObject getUserRole :getUserRoles){
                    if (getAllUser.getString("userId").equals(getUserRole.getString("userId")))
                        getAllUser.put("roleLists",getUserRoles);
                }
        }
        return CommonUtil.successPage(jsonObject,getAllUsers,count);
    }

    /**
     * 查询所有用户:分页+模糊查询
     */
    @Override
    public JSONObject getAllUser(JSONObject jsonObject) {
        CommonUtil.fillPageParam(jsonObject);
        int count = userDao.countUser(jsonObject);
        List<JSONObject> getAllUser = userDao.getAllUser(jsonObject);
        return CommonUtil.successPage(jsonObject,getAllUser,count);
    }

    /**
     *  查询所有角色：分页+模糊查询
     */
    @Override
    public JSONObject listRoleAll(JSONObject jsonObject) {
        CommonUtil.fillPageParam(jsonObject);
        int count = userDao.countRole(jsonObject);
        List<JSONObject> getAllRole = userDao.listRoleAll(jsonObject);
        return CommonUtil.successPage(jsonObject,getAllRole,count);

    }
    /**
     * 根据用户ID查询所属角色/部门ID
     */
    @Override
    public JSONObject DepartmentIdRoleIdByUserId(JSONObject jsonObject) {
        JSONObject jsonObjects=new JSONObject();
        //部门ID
        List<JSONObject> jsonObjectDepartmentId = userDao.DepartmentIdByUserId(jsonObject);
        jsonObjects.put("jsonObjectDepartmentId",jsonObjectDepartmentId);
        //角色ID
        List<JSONObject> jsonObjectRoleId = userDao.RoleIdByUserId(jsonObject);
        jsonObjects.put("jsonObjectRoleId",jsonObjectRoleId);
        return CommonUtil.successJson(jsonObjects);
    }

    /**
     *  根据部门ID：查询所属部门的用户信息
     */
    @Override
    public JSONObject userBydepartmentId(JSONObject jsonObject) {
        String a= jsonObject.getString("departmentId");
        CommonUtil.fillPageParam(jsonObject);
        int count = userDao.userBydepartmentIdCount(jsonObject);
        List<JSONObject> jsonObjects = userDao.userBydepartmentId(jsonObject);
        return CommonUtil.successPage(jsonObject,jsonObjects,count);
    }

    /**
     * 用户列表
     */
    @Override
    public JSONObject listUser(JSONObject jsonObject) {
        CommonUtil.fillPageParam(jsonObject);
        int count = userDao.countUser(jsonObject);
        List<JSONObject> list = userDao.listUser(jsonObject);
        return CommonUtil.successPage(jsonObject, list, count);
    }


    /**
     * 添加用户
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject addUser(JSONObject jsonObject) {
        //根据接收前台用户姓名判断是否已存在 添加用户数据
        int exist = userDao.queryExistUsername(jsonObject);
        if (exist > 0) {
            return CommonUtil.errorJson(ErrorEnum.E_10009);
        }
        // 效验密码长度
        if (jsonObject.getString("password").length() > Constant.PASSWORD_MAX_LEN) {
            throw new CustomException("密码最多8位(Password up to 8 bits.)");
        }
        // 密码以帐号+密码的形式进行AES加密
        String passwordKey = AesCipherUtil.enCrypto(jsonObject.getString("password") + jsonObject.getString("userName"));
        jsonObject.put("passwordKey",passwordKey);
        //生成UUID
        String uid = UUID.randomUUID().toString();
        jsonObject.put("uid",uid);
        //添加用户
        int res = userDao.addUser(jsonObject);
        if (res <= 0){
            throw new CustomException("新增用户失败(Insert User Failure)");
        }
        //根据接收前台用户姓名查询添加数据的userId
        int userId = userDao.getUserId(jsonObject.getString("userName"));
        //添加部门
        System.out.println("*******************"+userId+"***********************");
        if(jsonObject.get("departmentId") != null || jsonObject.get("departmentId") != ""){

           int count = userDao.addUserDepartmentId(jsonObject.getInteger("userId"), (List<Integer>) jsonObject.get("departmentId"));
            if (count <= 0){
                throw new CustomException("新增部门失败(Insert Department Failure)");
            }
        }

        return CommonUtil.successJson();
    }



    /**
     * 查询所有的角色
     * 在添加/修改用户的时候要使用此方法
     */
    @Override
    public JSONObject getAllRoles() {
        List<JSONObject> roles = userDao.getAllRoles();
        return CommonUtil.successPage(roles);
    }



    /**
     *  软删除：根据userId修改delete状态为2(1:正常、2删除)
     */
    @Override
    public JSONObject delUser(JSONObject jsonObject) {
        String b =jsonObject.getString("userId");
        userDao.delUser((List<Integer>) jsonObject.get("userId"));
        return CommonUtil.successJson();
    }
    /**
     *	修改用户状态 1正常 2异常 deleteStatus
     */
    @Override
    public JSONObject updateUser(JSONObject jsonObject) {
        userDao.updateUser(jsonObject);
        return CommonUtil.successJson();
    }

    /**
     * 修改用户
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject updateUsers(JSONObject jsonObject) {
        //修改用户信息
        int res = userDao.updateUser(jsonObject);
        if (res == 0)
            CommonUtil.errorJson(ErrorEnum.valueOf("修改用户信息失败,请重新尝试"));
        //获取前端传过来的userId
        Integer userId = jsonObject.getInteger("userId");
        List<Integer> newDepartments = (List<Integer>) jsonObject.get("departmentId");
        //如果用户在前端没有勾选部门信息/ 全部改为 delete:2
        if (newDepartments == null || newDepartments.size() == 0){
            userDao.removeOldDepartmentAll(userId);
            return CommonUtil.successJson(ErrorEnum.valueOf("此用户已清除部门信息！"));
        }else {

            //根据前端获取的roleId修改delete为1
            userDao.updateDeleteByDepartmentId((List<Integer>) jsonObject.get("departmentId"));
            //数据库取出数据旧的角色信息
            Set<Integer> oldDepartments = userDao.getUserDepartmentId(jsonObject);
            //添加新权限
            addNewUserDepartments(userId, newDepartments, oldDepartments);
            //移除旧的不再拥有的权限
            deleteOldUserDepartments(userId, newDepartments, oldDepartments);
        }
        return CommonUtil.successJson();
    }

    /**
     * 为用户添加新的部门
     */
    private void addNewUserDepartments(Integer userId, Collection<Integer> newDepartments, Collection<Integer> oldDepartments) {
        List<Integer> waitInsert = new ArrayList<>();
        for (Integer newDepartment : newDepartments) {
            if (!oldDepartments.contains(newDepartment)) {
                waitInsert.add(newDepartment);
            }
        }
        if (waitInsert.size() > 0) {
            userDao.addUserDepartmentId(userId, waitInsert);
        }
    }

    /**
     * 删除用户旧的部门
     */
    private void deleteOldUserDepartments(Integer userId, Collection<Integer> newDepartments, Collection<Integer> oldDepartments) {
        List<Integer> waitRemove = new ArrayList<>();
        for (Integer oldDepartment : oldDepartments) {
            if (!newDepartments.contains(oldDepartment)) {
                waitRemove.add(oldDepartment);
            }
        }
        if (waitRemove.size() > 0) {
            userDao.deleteOldUserDepartment(userId, waitRemove);
        }
    }





    /**
     *  用户修改角色
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject updateUserRole(JSONObject jsonObject) {
        Integer userId = jsonObject.getInteger("userId");
        //添加新的角色
        List<Integer> newRoles = (List<Integer>) jsonObject.get("roleId");
        //如果用户在前端没有勾选部门信息/ 全部改为 delete:2
        if (newRoles == null || newRoles.size() == 0){
            userDao.removeOldRoleAll(userId);
            return CommonUtil.successJson(ErrorEnum.valueOf("此用户已清除角色信息！"));
        }else {
            //根据前端获取的roleId修改delete为1
            userDao.updateDeleteByRoleId((List<Integer>) jsonObject.get("roleId"));
            //数据库取出数据旧的角色信息
            Set<Integer> oldRoles = userDao.getUserRole(jsonObject);
            //添加新权限
            addNewUserRole(userId, newRoles, oldRoles);
            //移除旧的不再拥有的权限
            deleteOldUserRole(userId, newRoles, oldRoles);
        }
        return CommonUtil.successJson();
    }

    /**
     * 为用户添加新的角色
     */
    private void addNewUserRole(Integer userId, Collection<Integer> newRoles, Collection<Integer> oldRoles) {
        List<Integer> waitInsert = new ArrayList<>();
        for (Integer newRole : newRoles) {
            if (!oldRoles.contains(newRole)) {
                waitInsert.add(newRole);
            }
        }
        if (waitInsert.size() > 0) {
            userDao.addNewUserRole(userId, waitInsert);
        }
    }

    /**
     * 删除用户旧的角色
     */
    private void deleteOldUserRole(Integer userId, Collection<Integer> newRoles, Collection<Integer> oldRoles) {
        List<Integer> waitRemove = new ArrayList<>();
        for (Integer oldRole : oldRoles) {
            if (!newRoles.contains(oldRole)) {
                waitRemove.add(oldRole);
            }
        }
        if (waitRemove.size() > 0) {
            userDao.deleteOldUserRole(userId, waitRemove);
        }
    }




    /**
     * 添加用户直接权限
     */
    @Override
    public JSONObject addUserPermission(JSONObject jsonObject) {
        //UUID
        String upid = UUID.randomUUID().toString();
        jsonObject.put("upid",upid);
        userDao.addUserPermission(upid,jsonObject.getInteger("userId"), (List<Integer>) jsonObject.get("permissionId"));
        return CommonUtil.successJson();
    }

    /**
     * 根据用户ID查询直接权限ID
     */
    @Override
    public JSONObject getPermissionByUserId(JSONObject jsonObject) {
        List <JSONObject> permissionId= userDao.getPermissionByUserId(jsonObject);
        return CommonUtil.successJson(permissionId);
    }

    /**
     *	修改用户直接权限
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject updateUserPermission(JSONObject jsonObject) {
        //获取前端roleId
        Integer userId = jsonObject.getInteger("userId");
        //添加新的权限(菜单)
        List<Integer> newpermissions = (List<Integer>) jsonObject.get("permissionId");
        //如果用户在前端没有勾选部门信息/ 全部改为 delete:2
        if (newpermissions == null || newpermissions.size() == 0){
            userDao.removeOldUserPermissionAll(userId);
            return CommonUtil.successJson(ErrorEnum.valueOf("此匿名用户已清除权限信息！"));
        }else {
            //根据前端获取的permissionId修改delete为1
            userDao.updateUserPermissionByUserId((List<Integer>) jsonObject.get("permissionId"));
            //数据库查出部门旧的权限(菜单)
            Set<Integer> oldpermissions = userDao.getpermissionIdByUserId(jsonObject);
            //添加新权限
            addUserPermission(userId, newpermissions, oldpermissions);
            //移除旧的不再拥有的权限
            deleteOldUserPermission(userId, newpermissions, oldpermissions);
        }

        return CommonUtil.successJson();
    }





    private void addUserPermission(Integer userId, Collection<Integer> newpermissions, Collection<Integer> oldpermissions) {
        List<Integer> waitInsert = new ArrayList<>();
        for (Integer newpermission : newpermissions) {
            if (!oldpermissions.contains(newpermission)) {
                waitInsert.add(newpermission);
            }
        }
        //UUID
        String drpid = UUID.randomUUID().toString();
        if (waitInsert.size() > 0) {
            userDao.addUserPermission(drpid,userId, waitInsert);
        }
    }


    private void deleteOldUserPermission(Integer userId, Collection<Integer> newpermissions, Collection<Integer> oldpermissions) {
        List<Integer> waitRemove = new ArrayList<>();
        for (Integer oldpermission : oldpermissions) {
            if (!newpermissions.contains(oldpermission)) {
                waitRemove.add(oldpermission);
            }
        }

        if (waitRemove.size() > 0) {
            userDao.deleteOldUserPermission(userId, waitRemove);
        }
    }

    /**
     * 角色列表
     */
    @Override
    public JSONObject listRole() {
        List<JSONObject> roles = userDao.listRole();
        return CommonUtil.successPage(roles);
    }

    /**
     * 查询所有权限, 给角色分配权限时调用
     */
    @Override
    public JSONObject listAllPermission() {
        List<JSONObject> permissions = userDao.listAllPermission();
        return CommonUtil.successPage(permissions);
    }

    /**
     *  添加新角色
     */
    @Override
    public JSONObject addNewRole(JSONObject jsonObject) {
        // 1.添加角色
        //UUID
        String rid = UUID.randomUUID().toString();
        jsonObject.put("rid",rid);
        String parentIdNulll =jsonObject.getString("parentId");
        String descNulll =jsonObject.getString("desc");
        if (StringTools.isNullOrEmpty(parentIdNulll)){
            jsonObject.put("parentId",null);
        }

        userDao.addNewRole(jsonObject);
        // 2.为角色选择权限+菜单

        return CommonUtil.successJson();
    }

    /**
     * 	删除角色：删除角色：软删除 根据delete(1正常 2删除,前台不显示)
     */
    @Override
    public JSONObject delNewRole(JSONObject jsonObject) {
        //删除角色：根据roleId
        int res = userDao.delNewRole(jsonObject);
        //删除成功
        if (res>0){
            return CommonUtil.successDelJson();
            //删除失败
        }else {
            return CommonUtil.errDelJson();
        }
    }

    /**
     * 	修改角色状态 delete_status 1 正常 2异常
     */
    @Override
    public JSONObject updateStatusByRoleId(JSONObject jsonObject) {
        System.out.println(jsonObject.getString("deleteStatus"));
        System.out.println((List<Integer>) jsonObject.get("roleId"));
        userDao.updateStatusByRoleId(jsonObject.getString("deleteStatus"),(List<Integer>) jsonObject.get("roleId"));
        return CommonUtil.successJson();
    }

    /**
     *  修改角色："roleId,parentId,roleName,desc,deleteStatus"
     */
    @Override
    public JSONObject updateNewRole(JSONObject jsonObject) {
        userDao.updateNewRole(jsonObject);
        return CommonUtil.successJson();
    }



    /**
     * 为角色选取菜单
     */
    @Override
    public JSONObject addRoleMenu(JSONObject jsonObject) {
        //获取前端departmentId
        Integer roleId = jsonObject.getInteger("roleId");
        //获取新的角色菜单
        List<Integer> newpermissions = (List<Integer>) jsonObject.get("permissionId");
        //效验新增角色菜单是否存在
        Set<Integer> oldpermissions = userDao.getPermissionIdByRoleId(jsonObject);
        List<Integer> waitInsert = new ArrayList<>();
        for (Integer newpermission : newpermissions) {
            if (!oldpermissions.contains(newpermission)) {
                waitInsert.add(newpermission);
            }
        }
        //UUID
        String drpid = UUID.randomUUID().toString();
        jsonObject.put("drpid",drpid);
        if (waitInsert.size() > 0) {
            userDao.insertRolePermission(drpid,roleId, (List<Integer>) jsonObject.get("permissionId"));
        }
        return CommonUtil.successJson();
    }

    /**
     * 修改角色权限
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject updateRolePermission(JSONObject jsonObject) {
        //获取前端roleId
        Integer roleId = jsonObject.getInteger("roleId");
        System.out.println(roleId+"****************************");
        //添加新的权限(菜单)
        List<Integer> newpermissions = (List<Integer>) jsonObject.get("permissionId");
        if (newpermissions == null || newpermissions.size() == 0){
            userDao.removeOldPermissionAll(roleId);
            return  CommonUtil.successJson(ErrorEnum.valueOf("此角色权限已清除！"));
        }else {
     /*       //添加新的权限(菜单)
            List<Integer> newpermissions = (List<Integer>) jsonObject.get("permissionId");*/
            System.out.println("**************" + jsonObject.get("permissionId"));
            //根据前端获取的departmentId修改delete为1
            userDao.updateDeleteByDepartmentId2((List<Integer>) jsonObject.get("permissionId"));
            //数据库查出部门旧的权限(菜单)
            Set<Integer> oldpermissions = userDao.getPermissionIdByRoleId(jsonObject);
            //添加新权限
            insertRolePermission(roleId, newpermissions, oldpermissions);
            //移除旧的不再拥有的权限
            deleteOldPermission(roleId, newpermissions, oldpermissions);
        }
        return CommonUtil.successJson();
    }


    /**
     * 修改   为角色添加新权限
     */
    private void insertRolePermission(Integer roleId, Collection<Integer> newpermissions, Collection<Integer> oldpermissions) {
        List<Integer> waitInsert = new ArrayList<>();
        for (Integer newpermission : newpermissions) {
            if (!oldpermissions.contains(newpermission)) {
                waitInsert.add(newpermission);
            }
        }
        //UUID
        String drpid = UUID.randomUUID().toString();
        if (waitInsert.size() > 0) {
            userDao.insertRolePermission(drpid,roleId, waitInsert);
        }
    }

    /**
     * 删除角色 旧的 不再拥有的权限
     */
    private void deleteOldPermission(Integer roleId, Collection<Integer> newpermissions, Collection<Integer> oldpermissions) {
        List<Integer> waitRemove = new ArrayList<>();
        for (Integer oldpermission : oldpermissions) {
            if (!newpermissions.contains(oldpermission)) {
                waitRemove.add(oldpermission);
            }
        }

        if (waitRemove.size() > 0) {
            userDao.removeOldPermission(roleId, waitRemove);
        }
    }
    /**
     * 根据角色Id查询权限Id
     */
    @Override
    public JSONObject permissionIdByRoleId(JSONObject jsonObject) {
        List<JSONObject> permissionId = userDao.permissionIdByRoleId(jsonObject);
        return CommonUtil.successJson(permissionId);
    }

    /**
     * 删除角色
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject deleteRole(JSONObject jsonObject) {
        JSONObject roleInfo = userDao.getRoleAllInfo(jsonObject);
        List<JSONObject> users = (List<JSONObject>) roleInfo.get("users");
        if (users != null && users.size() > 0) {
            return CommonUtil.errorJson(ErrorEnum.E_10008);
        }
        userDao.removeRole(jsonObject);
        userDao.removeRoleAllPermission(jsonObject);
        return CommonUtil.successJson();
    }

    /**
     * 部门列表
     */
    @Override
    public JSONObject listDepartment() {
        List<JSONObject> listDepartment = userDao.listDepartment();
        return CommonUtil.successPage(listDepartment);
    }


    /**
     * 单纯部门列表：前端选择部门列表
     */
    @Override
    public JSONObject getAllDepartment(JSONObject jsonObject) {
        //分页参数
        CommonUtil.fillPageParam(jsonObject);
        //查询数据
        List<JSONObject> getAllDepartment = userDao.getAllDepartment(jsonObject);
        //数据条数
        int count = userDao.countDepartment(jsonObject);
        return CommonUtil.successPage(jsonObject,getAllDepartment,count);
    }

    @Override
    public JSONObject treeMenuDepartment(JSONObject jsonObject) {
        String res = jsonObject.getString("list");
        System.out.println(res+"************************************");
        //查询数据
        MenuTreeUtil menuTreeUtil = new MenuTreeUtil();
        List<Object> treeMenuDepartmentMap = menuTreeUtil.treeMenuDepartmentMap(null,0,jsonObject);
        return CommonUtil.successJson(treeMenuDepartmentMap);
    }

    /*    *//**
     *  树形部门展示：list
     *//*
    @Override
    public List<MenuDepartment> treeMenuDepartment(JSONObject jsonObject) {
        MenuTreeUtil menuTreeUtil = new MenuTreeUtil();
        return menuTreeUtil.treeMenuDepartment(jsonObject);
    }*/
    /**
     * 删除部门 delete
     */
    @Override
    public JSONObject deleteByDepartmentId(JSONObject jsonObject) {
        userDao.deleteByDepartmentId(jsonObject);
        return CommonUtil.successJson();
    }
    /**
     * 修改状态	delete_status 修改：2 软删除
     */
    @Override
    public JSONObject deleteStatusByDepartmentId(JSONObject jsonObject) {

        System.out.println(jsonObject.getString("deleteStatus"));
        System.out.println((List<Integer>) jsonObject.get("departmentId"));
        userDao.deleteStatusByDepartmentId(jsonObject.getString("deleteStatus"),(List<Integer>) jsonObject.get("departmentId"));
        return CommonUtil.successJson();
    }
    /**
     * 新增部门
     */
    @Override
    public JSONObject addNewDepartment(JSONObject jsonObject) {
        //根据接收前台用户姓名判断是否已存在 添加用户数据
        int exist = userDao.queryExistDepartmentUsername(jsonObject);
        if (exist > 0) {
            return CommonUtil.errorJson(ErrorEnum.E_70009);
        }
        //UUID
        String did = UUID.randomUUID().toString();
        jsonObject.put("did",did);
        userDao.addNewDepartment(jsonObject);
        return CommonUtil.successJson();
    }

    /**
     * 为部门选择菜单
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject addNewPermissionByDepartmentId(JSONObject jsonObject) {
        //获取前端departmentId
        Integer departmentId = jsonObject.getInteger("departmentId");
        //获取新的部门菜单
        List<Integer> newDepartments = (List<Integer>) jsonObject.get("permissionId");
        //效验新增部门菜单是否存在
        Set<Integer> oldDepartments = userDao.getDepartmentId(jsonObject);

        List<Integer> waitInsert = new ArrayList<>();
        for (Integer newDepartment : newDepartments) {
            if (!oldDepartments.contains(newDepartment)) {
                waitInsert.add(newDepartment);
            }
        }
        //UUID
        String drpid = UUID.randomUUID().toString();

        jsonObject.put("drpid",drpid);
        if (waitInsert.size() > 0) {
            userDao.addNewPermissionByDepartmentId(drpid,departmentId,waitInsert );
        }
        return CommonUtil.successJson();
    }


    /**
     * 根据部门Id获取权限id
     */
    @Override
    public JSONObject permissionIdByDepartmentId(JSONObject jsonObject) {
        List<JSONObject> jsonObjects = userDao.permissionIdByDepartmentId(jsonObject);
        return CommonUtil.successJson(jsonObjects);
    }
    /**
     * 修改部门
     */
    @Override
    public JSONObject updateDepartment(JSONObject jsonObject) {
        userDao.updateDepartment(jsonObject);
        return CommonUtil.successJson();
    }


    /**
     * 修改部门菜单
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject updateDepartmentPermission(JSONObject jsonObject) {
        //获取前端departmentId
        Integer departmentId = jsonObject.getInteger("departmentId");
        //添加新的权限(菜单)
        List<Integer> newDepartments = (List<Integer>) jsonObject.get("permissionId");
        if (newDepartments == null || newDepartments.size() == 0){
            userDao.deleteOldPermissionAllByDepartment(departmentId);
            return  CommonUtil.successJson(ErrorEnum.valueOf("此部门权限已清除！"));
        } else {
            //根据前端获取的departmentId修改delete为1
            userDao.updateDeleteByDepartmentId2((List<Integer>) jsonObject.get("permissionId"));
            //数据库查出部门旧的权限(菜单)
            Set<Integer> oldDepartments = userDao.getDepartmentId(jsonObject);
            //添加新权限
            addNewDepartment(departmentId, newDepartments, oldDepartments);
            //移除旧的不再拥有的权限
            deleteOldDepartment(departmentId, newDepartments, oldDepartments);
        }
        return CommonUtil.successJson();
    }


    /**
     *  添加部门新的权限
     */
    private void addNewDepartment(Integer departmentId, Collection<Integer> newDepartments, Collection<Integer> oldDepartments) {
        List<Integer> waitInsert = new ArrayList<>();
        for (Integer newDepartment : newDepartments) {
            if (!oldDepartments.contains(newDepartment)) {
                waitInsert.add(newDepartment);
            }
        }
        //UUID
        String drpid = UUID.randomUUID().toString();
        if (waitInsert.size() > 0) {
            userDao.addNewPermissionByDepartmentId(drpid,departmentId, waitInsert);
        }
    }

    /**
     *  删除部门旧的权限
     */
    private void deleteOldDepartment(Integer departmentId, Collection<Integer> newDepartments, Collection<Integer> oldDepartments) {
        List<Integer> waitRemove = new ArrayList<>();
        for (Integer oldDepartment : oldDepartments) {
            if (!newDepartments.contains(oldDepartment)) {
                waitRemove.add(oldDepartment);
            }
        }
        if (waitRemove.size() > 0) {
            userDao.deleteOldPermissionByDepartment(departmentId, waitRemove);
        }
    }

    /**
     *  查询所有权限及菜单：分页+模糊
     */
    @Override
    public JSONObject getAllPermission(JSONObject jsonObject) {
        //分页参数处理
        CommonUtil.fillPageParam(jsonObject);
        //统计总条数
        int count = userDao.countPermission(jsonObject);
        //查询所有权限菜单
        List<JSONObject> getAllPermission= userDao.getAllPermission(jsonObject);
        return CommonUtil.successPage(jsonObject,getAllPermission,count);
    }
    /**
     *  添加菜单
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject addNewPermission(JSONObject jsonObject) {
        System.out.println(jsonObject.getString("menuPermissionName"));
        //1、效验菜单是否已存在
        String menuPermissionName = userDao.queryExistmenuName(jsonObject.getString("menuPermissionName"));
        if (StringTools.isNullOrEmpty(menuPermissionName)){
            // 2.如果parentId != null || "" ,根据parentId 查询出 grade：等级  在新增时为子集添加grade时使用原来的等级+1：新等级
            if (!StringTools.isNullOrEmpty(jsonObject.getString("parentId"))){
                List<String>  grades =userDao.gradeByPermissionParentId(jsonObject.getString("parentId"));
                for (String grade : grades){
                    if (!StringTools.isNullOrEmpty(grade)){
                        int newGrade = Integer.valueOf(grade) +1;
                        jsonObject.put("grade",newGrade);
                    }
                }
            }else {
                // 添加菜单等级
                jsonObject.put("grade",1);
            }
            //UUID
            jsonObject.put("pid",UUID.randomUUID().toString());
            //3.添加菜单
            userDao.addNewPermission(jsonObject);

        }else if (menuPermissionName.equals(jsonObject.getString("menuPermissionName"))){
            return CommonUtil.successJson(ErrorEnum.valueOf("菜单已存在,请勿重新添加"));
        }
        return CommonUtil.successJson();
    }
    /**
     *  删除菜单
     */
    @Override
    public JSONObject delNewPermission(JSONObject jsonObject) {
        //1.循环permissionId：批量删除
        int res = userDao.delNewPermission((List<Integer>) jsonObject.get("permissionId"));
        //删除成功
        if (res>0){
            //2.根据循环permissionId删除对应表菜单权限
            userDao.delNewPermissionToDepartmentRolePermission((List<Integer>) jsonObject.get("permissionId"));
            return CommonUtil.successDelJson();
            //删除失败
        }else {
            return CommonUtil.errDelJson();
        }
    }

    /**
     *  修改菜单状态
     */
    @Override
    public JSONObject updateNewPermissionStatusById(JSONObject jsonObject) {
        System.out.println(jsonObject.getString("deleteStatus"));
        System.out.println((List<Integer>) jsonObject.get("permissionId"));
        userDao.updateNewPermissionStatusById(jsonObject.getString("deleteStatus"),(List<Integer>) jsonObject.get("permissionId"));
        return CommonUtil.successJson();
    }

    /**
     *  更新菜单
     */
    @Override
    public JSONObject updateNewPermission(JSONObject jsonObject) {
        int res = userDao.updateNewPermission(jsonObject);
        System.out.println(res+"=============================");
        return CommonUtil.successJson();
    }

}

