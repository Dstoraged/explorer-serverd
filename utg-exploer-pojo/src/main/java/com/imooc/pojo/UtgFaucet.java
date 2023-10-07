package com.imooc.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class UtgFaucet {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="address")
    private String address;

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @Column(name = "hash")
    private String hash;


    @Column(name = "num")
    private BigDecimal num;

}
