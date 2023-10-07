package com.imooc.utils;

import java.io.Serializable;

public class Result  implements Serializable {
    private static final long serialVersionUID = 7167899422279472093L;

    private String resultDesc;

    private int statusCode;

    public Result(String resultDesc ,int statusCode) {
        super();
        this.resultDesc = resultDesc;
        this.statusCode = statusCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


    public static Result getResult(String resultDesc, int statusCode) {
        return new Result(resultDesc, statusCode);
    }

}
