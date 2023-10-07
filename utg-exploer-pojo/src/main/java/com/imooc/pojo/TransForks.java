package com.imooc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Column;

@Data
@TableName("")
public class TransForks {

    @Column(name="id")
    private  long id;

    @Column(name="nephewhash")
    private  String nephewHash;

    @Column(name="nephewblock")
    private  String nephewBlock;

    @Column(name="unclehash")
    private  String uncleHash;
    @Column(name="uncleblock")
    private  String uncleBlock;



}
