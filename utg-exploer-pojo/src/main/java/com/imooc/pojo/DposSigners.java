package com.imooc.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class DposSigners {

    private long id ;

    private Date loopStartTime;

    private  String singerAddress;

    private  Integer punished;




}
