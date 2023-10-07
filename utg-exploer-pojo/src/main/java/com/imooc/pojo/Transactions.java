package com.imooc.pojo;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Transactions {
    @Column(name = "hash")
    private String hash;

    @Column(name = "istrunk")
    private Integer isTrunk;

    @Column(name = "timestamp")
    private Date timeStamp;

    @Column(name = "fromaddr")
    private String fromAddr;

    @Column(name = "toaddr")
    private String toAddr;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "nonce")
    private Long nonce;

    @Column(name = "gaslimit")
    private Long gasLimit;

    @Column(name = "gasprice")
    private Long gasPrice;

    @Column(name = "status")
    private  Integer  status;

    @Column(name = "gasused")
    private Long gasUsed;

    @Column(name = "cumulative")
    private Long cumulative;

    @Column(name = "blockhash")
    private String  blockHash;

    @Column(name = "blocknumber")
    private Long blockNumber;

    @Column(name = "blockindex")
    private Integer blockIndex;

    @Column(name = "input")
    private String  input;

    @Column(name = "contract")
    private String  contract;

    @Column(name = "error")
    private String error;

    @Column(name = "internal")
    private Integer  internal;
}
