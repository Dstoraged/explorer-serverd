package com.imooc.pojo.vo;

import lombok.Data;
@Data
public class TransactionVo {

    private  String  ufoprefix; //Prefix

    private  String ufoversion; //version

    private  String ufooperator; //operate

    public TransactionVo(String ufoprefix, String ufoversion, String ufooperator) {
        this.ufoprefix = ufoprefix;
        this.ufoversion = ufoversion;
        this.ufooperator = ufooperator;
    }
}
