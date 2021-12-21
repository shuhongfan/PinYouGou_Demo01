package com.shf.pyg.entity;

import java.io.Serializable;

/**
 * 返回结果实体类
 */
public class Result implements Serializable {
//    是否成功
    private boolean success;
    private String messgae;

    public Result() {
    }

    public Result(boolean success, String messgae) {
        this.success = success;
        this.messgae = messgae;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", messgae='" + messgae + '\'' +
                '}';
    }
}
