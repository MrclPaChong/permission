package com.meishutech.permission.util.tools;

/**
 * 判空工具类
 * @author cl
 * @company XX公司
 * @create 2019-09-22 9:31
 */
public class StringTools {

	public static boolean isNullOrEmpty(String str) {
		return null == str || "".equals(str) || "null".equals(str);
	}

	public static boolean isNullOrEmpty(Object obj) {
		return null == obj || "".equals(obj);
	}
}
