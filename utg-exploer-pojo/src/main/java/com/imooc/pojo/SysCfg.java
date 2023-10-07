package com.imooc.pojo;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class SysCfg {
    @Column(name="id")
    private Long id;

    @Column(name="cfgvalue")
    private Long cfgValue;

    @Column(name="cfgdesc")
    private  String cfgDesc;

    @Column(name="cfgname")
    private  String cfgName;

    @Column(name="createtime")
    private Date createTime;

    @Column(name="createby")
    private Date createBy;

    @Column(name="modifytime")
    private Date modifyTime;

    @Column(name="modifyby")
    private Long modifyBy;

}
