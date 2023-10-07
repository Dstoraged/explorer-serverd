package com.imooc.pojo;

import lombok.Data;

import javax.persistence.Column;

@Data

public class Contracts {

    @Column(name = "hash")
    private  String hash;

    @Column(name = "transhash")
    private  String transHash;

    @Column(name = "istoken")
    private  Integer isToken;
}
