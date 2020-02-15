package com.meishutech.permission.model.tree;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * @author: cl
 * @description: MyBatis的一对多JSON返回对象
 * <p>
 * 处理嵌套查询结果时，MyBatis会根据bean定义的属性类型来初始化嵌套的成员变量，
 * 主要看其是不是Collection
 * 如果这里不定义，那么嵌套返回结果里就只能返回一对一的结果，而不是一对多的
 * <p>
 * 参见MyBatis  DefaultResultSetHandler.instantiateCollectionPropertyIfAppropriate()
 * @date: 2019/09/22 10:16
 */
public class OneMany extends JSONObject {
	//Set中存储的数据是无顺序的，并且不允许重复
	//角色集合
	private Set<String> roleList;
	//部门集合
	private Set<String> departmentList;
	//菜单集合
	private Set<JSONObject> menuList;
	//权限集合
	private Set<String> permissionList;
	//权限ID
	private Set<Integer> permissionIds;

	//List中存储的数据是有顺序的，并且值允许重复
	private List<JSONObject> picList;
	private List<JSONObject> menus;
	private List<JSONObject> users;
	private List<JSONObject> permissions;
	//角色集合
	private List<String> roleLists;
	//部门集合
	private List<String> departmentLists;
}
