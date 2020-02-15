package com.meishutech.permission.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cl
 * @company XX公司
 * @create 2019-10-22 15:03
 */
@Data
public class User implements Serializable {

    //alt+enter add Uid
    private static final long serialVersionUID = 3856367352056054643L;

    /**
     * UUID
     */
    private String uid;

    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户真实姓名
     */
    private String nickname;

    /**
     * 联系方式
     */
    private String tel;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证
     */
    private String shenfen;

    /**
     * 管理员/普通用户
     */
    private String identity;

    /**
     * 状态 1正常 2异常
     */
    private String delete_status;

    /**
     * 是否删除 1正常 2异常
     */
    private String delete;

    /**
     * 创建时间
     */
    private String create_time;

    /**
     * 修改时间
     */
    private String update_time;
}
