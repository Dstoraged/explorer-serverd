package com.imooc.pojo;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
public class AddrBalances {

    @Column(name="id")
    private long id;

    @Column(name="address")
    private String address;

    @Column(name="contract")
    private String contract;

    @Column(name="blocknumber")
    private Long blockNumber;

    @Column(name="balance")
    private BigDecimal balance;

    @Column(name="nonce")
    private Long nonce;





}
