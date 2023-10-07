package com.imooc.enums;

/**
 * @author zhc 2021-09-18 13:11
 */
public enum MinerStatusEnum {
    mining(1,"mining"),
    toBeAddPool(2,"toBeAddPool"),
    exit(3,"exit");

    private Integer code;
    private String name;

    MinerStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
