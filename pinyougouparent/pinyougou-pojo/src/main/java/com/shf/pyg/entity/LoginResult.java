package com.shf.pyg.entity;

/**
 * 包含登录名的结果对象
 */
public class LoginResult {
    private boolean success;
    private String loginName;  //登录名
    private Object data; // 数据

    public LoginResult() {
    }

    public LoginResult(boolean success, String loginName, Object data) {
        this.success = success;
        this.loginName = loginName;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
