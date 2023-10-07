package com.imooc.pojo;

import lombok.Data;

import javax.persistence.Column;

@Data
public class TokenContract {


    @Column(name="id")
    private Long id;

    @Column(name="contractaddress")
    private  String contractAddress;

    @Column(name="contractmanager")
    private  String contractManager;

    private Long accountTotal;

}
