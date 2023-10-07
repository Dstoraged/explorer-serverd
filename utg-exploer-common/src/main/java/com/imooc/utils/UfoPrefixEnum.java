package com.imooc.utils;

/**
 * @author zhc 2021-09-08 13:12
 */
public enum UfoPrefixEnum {
    SSC("SSC"), UTG("UTG");
    private String code;
    UfoPrefixEnum(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
