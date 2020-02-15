package com.meishutech.permission.exception;

import com.alibaba.fastjson.JSONObject;

/**
 * 自定义异常(CustomException)
 * @author dolyw.com
 * @date 2018/8/30 13:59
 */
public class CustomException extends RuntimeException {

    private JSONObject resultJson;

    public CustomException(String msg){
        super(msg);
    }

    public CustomException() {
        super();
    }

    public CustomException(JSONObject resultJson) {
        this.resultJson = resultJson;
    }

    public JSONObject getResultJson() {
        return resultJson;
    }
}
