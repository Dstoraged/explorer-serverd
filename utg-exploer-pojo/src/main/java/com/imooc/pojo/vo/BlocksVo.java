package com.imooc.pojo.vo;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
@Data
public class BlocksVo {

    @Column(name = "hash")
    private String hash;

    @Column(name = "blocknumber")
    private Long blockNumber;

    @Column(name = "istrunk")
    private Integer isTrunk;

    @Column(name = "timestamp")
    private Date timeStamp;

    @Column(name = "mineraddress")
    private String minerAddress;

    @Column(name = "blocksize")
    private Integer blockSize;

    @Column(name = "gaslimit")
    private Long gasLimit;

    @Column(name = "gasused")
    private Long gasUsed;

    @Column(name = "reward")
    private BigDecimal reward;

    @Column(name = "txscount")
    private Integer txsCount;

    @Column(name = "nonce")
    private String nonce;

    @Column(name = "difficulty")
    private Long difficulty;

    @Column(name = "totaldifficulty")
    private Long totalDifficulty;

    @Column(name = "parenthash")
    private String parentHash;

    private Long avgGasPrice;

    private Integer blockTransactionCount;

    private BigDecimal lockamount;

    private BigDecimal releaseamount;

}
