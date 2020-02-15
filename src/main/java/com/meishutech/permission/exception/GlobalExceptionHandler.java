package com.meishutech.permission.exception;

import com.alibaba.fastjson.JSONObject;
import com.meishutech.permission.util.Result.CommonUtil;
import com.meishutech.permission.util.Result.ErrorEnum;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**	权限不足及未登录拦截
 * 统一异常拦截
 * @author: admin
 * @description:
 * @date: 2019-09-24 9:31
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@ExceptionHandler(value = Exception.class)
	public JSONObject defaultErrorHandler(HttpServletRequest req, Exception e) {
		String errorPosition = "";
		//如果错误堆栈信息存在
		if (e.getStackTrace().length > 0) {
			StackTraceElement element = e.getStackTrace()[0];
			String fileName = element.getFileName() == null ? "未找到错误文件" : element.getFileName();
			int lineNumber = element.getLineNumber();
			errorPosition = fileName + ":" + lineNumber;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", ErrorEnum.E_400.getErrorCode());
		jsonObject.put("msg", ErrorEnum.E_400.getErrorMsg());
		JSONObject errorObject = new JSONObject();
		errorObject.put("errorLocation", e.toString() + "    错误位置:" + errorPosition);
		jsonObject.put("info", errorObject);
		logger.error("异常", e);
		return jsonObject;
	}

	/**
	 * GET/POST请求方法错误的拦截器
	 * 因为开发时可能比较常见,而且发生在进入controller之前,上面的拦截器拦截不到这个错误
	 * 所以定义了这个拦截器
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public JSONObject httpRequestMethodHandler() {
		return CommonUtil.errorJson(ErrorEnum.E_500);
	}

	/**
	 * 权限不足报错拦截
	 */
	@ExceptionHandler(UnauthorizedException.class)
	public JSONObject unauthorizedExceptionHandler() {
		return CommonUtil.errorJson(ErrorEnum.E_502);
	}

	/**
	 * 未登录报错拦截
	 * 在请求需要权限的接口,而连登录都还没登录的时候,会报此错
	 */
	@ExceptionHandler(UnauthenticatedException.class)
	public JSONObject unauthenticatedException() {
		return CommonUtil.errorJson(ErrorEnum.E_20011);
	}
}
