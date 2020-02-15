package com.meishutech.permission.model.tree;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cl
 * @company XX公司
 * @create 2019-10-06 16:46
 */
@Data
public class MenuDepartment implements Serializable {

 /**
  * 查询树形结构数据
  */
   //部门UUID
    private String did;
   //部门ID
    private Integer id;
    //父级ID 、无默认0
    private String parent_id;
    //部门名称
    private String department_name;
    //深度 1-2-3级
    private String required_permission;
    //部门状态
    private String create_time;
    //部门状态
    private String update_time;
    //部门状态
    private String delete_status;
    //子菜单
    private List<MenuDepartment> childs;
}
