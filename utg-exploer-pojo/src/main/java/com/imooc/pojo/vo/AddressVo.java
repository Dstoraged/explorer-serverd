package com.imooc.pojo.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
@Data
public class AddressVo {
    @Id
    private Long id;

    @Column(name = "address")
    private String address;

    @Column(name = "contract")
    private String contract;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "nonce")
    private Long nonce;

    @Column(name = "blocknumber")
    private Long blockNumber;

    private String proportion;

    private Long toCount;

    @Column(name = "haslock")
    private Integer haslock;
    
    @Column(name = "balance_block")
    private Long balance_block;

    @Column(name = "srt_balance")
    private BigDecimal srt_balance;

    @Column(name = "srt_nonce")
    private Long srt_nonce;
    
    @Column(name = "srt_block")
    private Long srt_block;
    
    @Column(name = "isinner")
    private Integer isinner;
    @Column(name = "isstorage")
    private Integer isstorage;
    @Column(name = "isrevenue")
    private Integer isrevenue;
    @Column(name = "isminer")
    private Integer isminer;
    @Column(name = "iscontract")
    private Integer iscontract;

    private String appid;

    private String random;

    private String sign;

}
