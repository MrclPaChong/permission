package com.meishutech.permission.model.tree;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cl
 * @company XX公司
 * @create 2019-10-11 11:28
 */
@Data
public class MenuPermission implements Serializable {

    //菜单UUID
    private String pid;
    //权限ID
    private Integer id;
    //菜单名称
    private String menu_name;
    //菜单code
    private String menu_code;
    //权限名称
    private String permission_name;
    //权限code
    private String permission_code;
    //父级Id
    private String parent_id;
    //权限深度 1 、2 、3 级
    private String required_permission;
    //组件
    private String component;
    //菜单等级
    private String grade;
    //菜单排序
    private Integer sort;
    //图标
    private String icon;
    //菜单状态
    private String delete_status;
    //创建时间
    private String create_time;
    //修改时间
    private String update_time;
    //子菜单
    private List<MenuPermission> childs;
}
