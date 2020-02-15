package com.meishutech.permission.model.tree;

import lombok.Data;

import java.util.List;

/**
 * @author cl
 * @company XX公司
 * @create 2019-10-07 11:28
 */
@Data
public class MenuRole {

    //角色ID
    private Integer id;
    //角色名称
    private String role_name;
    //角色父级ID
    private String parent_id;
    //角色描述
    private String descs;
    //创建时间
    private String create_time;
    //修改时间
    private String update_time;
    //角色父级ID
    private String delete_status;
    //子菜单
    private List<MenuRole> childs;

}
