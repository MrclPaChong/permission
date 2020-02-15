package com.meishutech.permission.util.Result;

/**
 * 成功返回消息
 * @author: cl
 * @description: 通用常量类, 单个业务的常量请单开一个类, 方便常量的分类管理
 * @date: 2019/09/21 10:15
 */
public class Constants {

	/**
	 * jwtToken 设置token过期时间/毫秒
	 */
	public static final long EXPIRE =1000*60*60*1;//一小时

	/**
	 * 请求成功返回消息
	 */
	public static final String SUCCESS_CODE = "100";
	public static final String SUCCESS_MSG = "请求成功";
	public static final String SUCCESS_MSG_DEL = "删除成功";
	public static final String ERR_CODE = "400";
	public static final String SUCCESS_MSG_DEL_ERR = "删除失败";

	/**
	 * session中存放用户信息的key值
	 */
	public static final String SESSION_USER_INFO = "userInfo";
	public static final String SESSION_USER_PERMISSION = "userPermission";
}
