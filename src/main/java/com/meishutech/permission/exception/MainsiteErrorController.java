package com.meishutech.permission.exception;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.util.Result.CommonUtil;
import com.meishutech.permission.util.Result.ErrorEnum;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: cl
 * @description: 系统错误拦截, 主要是针对404的错误
 * 请求路径不存在
 * @date: 2019-09-23 9:39
 */
@Controller
public class MainsiteErrorController implements ErrorController {

	private static final String ERROR_PATH = "/error";

	/**
	 * 主要是登陆后的各种错误路径  404页面改为返回此json
	 * 未登录的情况下,大部分接口都已经被shiro拦截,返回让用户登录了
	 *
	 * @return 501的错误信息json
	 */
	@RequestMapping(value = ERROR_PATH)
	@ResponseBody
	public JSONObject handleError() {
		return CommonUtil.errorJson(ErrorEnum.E_501);
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}
}

