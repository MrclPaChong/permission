package com.meishutech.permission.util.tree;


import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.mapper.UserDao;
import com.meishutech.permission.model.tree.MenuDepartment;
import com.meishutech.permission.model.tree.MenuPermission;
import com.meishutech.permission.model.tree.MenuRole;
import com.meishutech.permission.util.tools.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名称：MenuTreeUtil
 * 类描述：递归构造树型结构
 */
@Component
public class MenuTreeUtil {
    //解决Utils无法注入server null 问题
    //https://blog.csdn.net/lzg319/article/details/80755445
    //https://blog.csdn.net/YyCarry/article/details/80663157

    @Autowired
    private UserDao userDao;

    private static UserDao staticUserDao;

    @PostConstruct
    public void init() {
        staticUserDao = userDao;
    }


    /**
     *  部门：知道最多几层机构的情况下，foreach不断排序列出，代码示例如下
     */
    public  List<MenuDepartment> treeMenuDepartment(JSONObject jsonObject) {
        List<MenuDepartment> vos = new ArrayList<>();
        List<MenuDepartment> systemRoleMenuEntities = staticUserDao.treeMenuDepartment(jsonObject);
        if (systemRoleMenuEntities.size() > 0) {
            //获取一级菜单
            for (MenuDepartment entity : systemRoleMenuEntities) {
                System.out.println("ss" + entity);
                if (StringTools.isNullOrEmpty(entity.getParent_id())) {
                    MenuDepartment vo = new MenuDepartment();
                    vo.setId(entity.getId());
                    vo.setDepartment_name(entity.getDepartment_name());
                    vo.setRequired_permission(entity.getRequired_permission());
                    vo.setParent_id(entity.getParent_id());
                    vos.add(vo);
                }
            }
            for (MenuDepartment vo : vos) {
                List<MenuDepartment> chrildrenOne = new ArrayList<>();
                for (MenuDepartment systemRoleMenuEntity : systemRoleMenuEntities) {
                    //二级菜单
                    if (vo.getId().equals(systemRoleMenuEntity.getParent_id())) {
                        System.out.println("二级菜单");
                        MenuDepartment menuTwo = new MenuDepartment();
                        menuTwo.setId(systemRoleMenuEntity.getId());
                        menuTwo.setParent_id(systemRoleMenuEntity.getParent_id());
                        menuTwo.setDepartment_name(systemRoleMenuEntity.getDepartment_name());
                        menuTwo.setRequired_permission(systemRoleMenuEntity.getRequired_permission());
                        List<MenuDepartment> ChrildrenTwo = new ArrayList<>();
                        for (MenuDepartment roleMenuEntity : systemRoleMenuEntities) {
                            System.out.println("三级菜单");
                            //三级菜单
                            if (menuTwo.getId().equals(roleMenuEntity.getParent_id())) {
                                MenuDepartment menuThree = new MenuDepartment();
                                menuThree.setId(roleMenuEntity.getId());
                                menuThree.setParent_id(roleMenuEntity.getParent_id());
                                menuThree.setDepartment_name(roleMenuEntity.getDepartment_name());
                                menuThree.setRequired_permission(roleMenuEntity.getRequired_permission());
                                List<MenuDepartment> chrildrenThree = new ArrayList<>();
                                for (MenuDepartment MenuEntityFour : systemRoleMenuEntities) {
                                    System.out.println("四级菜单");
                                    //四级菜单
                                    if (menuThree.getId().equals(MenuEntityFour.getParent_id())) {
                                        MenuDepartment MenusFour = new MenuDepartment();
                                        MenusFour.setId(MenuEntityFour.getId());
                                        MenusFour.setParent_id(MenuEntityFour.getParent_id());
                                        MenusFour.setDepartment_name(MenuEntityFour.getDepartment_name());
                                        MenusFour.setRequired_permission(MenuEntityFour.getRequired_permission());
                                        chrildrenThree.add(MenusFour);
                                    }
                                }
                                menuThree.setChilds(chrildrenThree);
                                ChrildrenTwo.add(menuThree);
                            }
                        }
                        menuTwo.setChilds(ChrildrenTwo);
                        chrildrenOne.add(menuTwo);
                    }
                }
                vo.setChilds(chrildrenOne);
            }
        }
        //}
        return vos;
    }


    /**
     *  部门：使用map加桶排序，进行递归查询
     */
    public List<Object>treeMenuDepartmentMap(List<String> ids, int i, JSONObject jsonObject) {
        List<MenuDepartment> vos = new ArrayList<>();
        List<Object> mapVos = new ArrayList<>();
        List<String> sortProperties = new ArrayList<>();
        List<MenuDepartment> entities = staticUserDao.treeMenuDepartment(jsonObject);
        for (MenuDepartment entity:entities) {
            Map<String,Object> menuMap = new LinkedHashMap<>();
            String res =String.valueOf(entity.getId());
            if (StringTools.isNullOrEmpty(entity.getParent_id())){
                menuMap.put("id",entity.getId());
                menuMap.put("parentId",entity.getParent_id());
                menuMap.put("departmentName",entity.getDepartment_name());
                menuMap.put("requiredPermission",entity.getRequired_permission());
                menuMap.put("createTime",entity.getCreate_time());
                menuMap.put("updateTime",entity.getUpdate_time());
                menuMap.put("deleteStatus",entity.getDelete_status());
                menuMap.put("departmentChildren",menuChild(res,entities,ids));
                mapVos.add(menuMap);
            }
        }
        return mapVos;
    }

    private List<?> menuChild(String id, List<MenuDepartment> entities,List<String> ids) {
        List<Object> child = new ArrayList<>();
        for (MenuDepartment entity:entities) {
            Map<String,Object> menuMap = new LinkedHashMap<>();
            String res =String.valueOf(entity.getId());
            String.valueOf(id);
            if (id.equals(entity.getParent_id())){
                menuMap.put("id",entity.getId());
                menuMap.put("parentId",entity.getParent_id());
                menuMap.put("departmentName",entity.getDepartment_name());
                menuMap.put("requiredPermission",entity.getRequired_permission());
                menuMap.put("createTime",entity.getCreate_time());
                menuMap.put("updateTime",entity.getUpdate_time());
                menuMap.put("deleteStatus",entity.getDelete_status());
                menuMap.put("departmentChildren",menuChild(res,entities,ids));
                child.add(menuMap);
            }
        }
        return child;
    }


    /**
     *  角色：树形列表：map
     */
    public List<Object>treeMenuRoleMap(List<String> ids, int i, JSONObject jsonObject) {
        List<MenuRole> vosMenuRole = new ArrayList<>();
        List<Object> mapVosMenuRole = new ArrayList<>();
        List<String> sortPropertiesMenuRole = new ArrayList<>();
        List<MenuRole> entitiesMenuRole = staticUserDao.listMenuRole(jsonObject);
        for (MenuRole entityMenuRole:entitiesMenuRole) {
            Map<String,Object> menuMap = new LinkedHashMap<>();
            if (StringTools.isNullOrEmpty(entityMenuRole.getParent_id())){
                String res =String.valueOf(entityMenuRole.getId());
                menuMap.put("id",entityMenuRole.getId());
                menuMap.put("menuRoleName",entityMenuRole.getRole_name());
                menuMap.put("parentId",entityMenuRole.getParent_id());
                menuMap.put("descs",entityMenuRole.getDescs());
                menuMap.put("createTime",entityMenuRole.getCreate_time());
                menuMap.put("updateTime",entityMenuRole.getUpdate_time());
                menuMap.put("deleteStatus",entityMenuRole.getDelete_status());
                menuMap.put("menuRoleChildren",menuChildMenuRole(res,entitiesMenuRole,ids));
                mapVosMenuRole.add(menuMap);
            }
        }
        return mapVosMenuRole;
    }

    private List<?> menuChildMenuRole(String id, List<MenuRole> entitiesMenuRole,List<String> ids) {
        List<Object> child = new ArrayList<>();
        for (MenuRole entityMenuRole:entitiesMenuRole) {
            Map<String,Object> menuMap = new LinkedHashMap<>();
            String res =String.valueOf(entityMenuRole.getId());
            String.valueOf(id);
            if (id .equals(entityMenuRole.getParent_id())){
                menuMap.put("id",entityMenuRole.getId());
                menuMap.put("menuRoleName",entityMenuRole.getRole_name());
                menuMap.put("parentId",entityMenuRole.getParent_id());
                menuMap.put("descs",entityMenuRole.getDescs());
                menuMap.put("createTime",entityMenuRole.getCreate_time());
                menuMap.put("updateTime",entityMenuRole.getUpdate_time());
                menuMap.put("deleteStatus",entityMenuRole.getDelete_status());
                menuMap.put("menuRoleChildren",menuChildMenuRole(res,entitiesMenuRole,ids));
                child.add(menuMap);
            }
        }
        return child;
    }


    /**treeMenuPermission
     * 菜单权限列表：map形式
     */
    public List<Object>treeMenuPermission(List<String> ids, int i, JSONObject jsonObject) {
        List<MenuPermission> vosMenuPermission = new ArrayList<>();
        List<Object> mapVosMenuRole = new ArrayList<>();
        List<String> sortPropertiesMenuPermission = new ArrayList<>();
        List<MenuPermission> entitiesMenuPermission = staticUserDao.treeMenuPermission(jsonObject);
        for (MenuPermission entityMenuPermission:entitiesMenuPermission) {
            Map<String,Object> menuMap = new LinkedHashMap<>();
            if (StringTools.isNullOrEmpty(entityMenuPermission.getParent_id())){
                String res =String.valueOf(entityMenuPermission.getId());
                menuMap.put("UUID",entityMenuPermission.getPid());
                menuMap.put("id",entityMenuPermission.getId());
                menuMap.put("menuPermissionName",entityMenuPermission.getMenu_name());
                menuMap.put("menuPermissionCode",entityMenuPermission.getMenu_code());
                menuMap.put("permissionName",entityMenuPermission.getPermission_name());
                menuMap.put("permissionCode",entityMenuPermission.getPermission_code());
                menuMap.put("parentId",entityMenuPermission.getParent_id());
                menuMap.put("requiredPermission",entityMenuPermission.getRequired_permission());
                menuMap.put("component",entityMenuPermission.getComponent());
                menuMap.put("grade",entityMenuPermission.getGrade());
                menuMap.put("sort",entityMenuPermission.getSort());
                menuMap.put("icon",entityMenuPermission.getIcon());
                menuMap.put("deleteStatus",entityMenuPermission.getDelete_status());
                menuMap.put("createTime",entityMenuPermission.getCreate_time());
                menuMap.put("updateTime",entityMenuPermission.getUpdate_time());
                menuMap.put("menuPermissionChildren",menuChildPermission(res,entitiesMenuPermission,ids));
                mapVosMenuRole.add(menuMap);
            }
        }
        return mapVosMenuRole;
    }

    private List<?> menuChildPermission(String id, List<MenuPermission> entitiesMenuPermission,List<String> ids) {
        List<Object> child = new ArrayList<>();
        for (MenuPermission entityMenuPermission:entitiesMenuPermission) {
            Map<String,Object> menuMap = new LinkedHashMap<>();
            String res =String.valueOf(entityMenuPermission.getId());
            String.valueOf(id);
            if (id .equals(entityMenuPermission.getParent_id())){
                menuMap.put("UUID",entityMenuPermission.getPid());
                menuMap.put("id",entityMenuPermission.getId());
                menuMap.put("menuPermissionName",entityMenuPermission.getMenu_name());
                menuMap.put("menuPermissionCode",entityMenuPermission.getMenu_code());
                menuMap.put("permissionName",entityMenuPermission.getPermission_name());
                menuMap.put("permissionCode",entityMenuPermission.getPermission_code());
                menuMap.put("parentId",entityMenuPermission.getParent_id());
                menuMap.put("requiredPermission",entityMenuPermission.getRequired_permission());
                menuMap.put("component",entityMenuPermission.getComponent());
                menuMap.put("grade",entityMenuPermission.getGrade());
                menuMap.put("sort",entityMenuPermission.getSort());
                menuMap.put("icon",entityMenuPermission.getIcon());
                menuMap.put("deleteStatus",entityMenuPermission.getDelete_status());
                menuMap.put("createTime",entityMenuPermission.getCreate_time());
                menuMap.put("updateTime",entityMenuPermission.getUpdate_time());
                menuMap.put("menuPermissionChildren",menuChildPermission(res,entitiesMenuPermission,ids));
                child.add(menuMap);
            }
        }
        return child;
    }
}