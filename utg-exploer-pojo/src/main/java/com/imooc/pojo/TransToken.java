package com.imooc.pojo;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
public class TransToken {

    @Column(name="id")
    private long id;

    @Column(name="transhash")
    private String transHash;

    @Column(name="logindex")
    private Integer loginIndex;

    /*0:ERC20  1:ERC721*/
    @Column(name="cointype")
    private Integer coinType;

    @Column(name="fromaddr")
    private String fromAddr;

    @Column(name="toaddr")
    private String toAddr;

    @Column(name="amount")
    private BigDecimal amount;

    @Column(name="contract")
    private  String  contract;

    @Column(name="blockhash")
    private String blockHash;

    @Column(name="blocknumber")
    private Long blockNumber;

    @Column(name="tokenid")
    private BigInteger tokenId;

    @Column(name="gasused")
    private Long gasUsed;

    @Column(name="gasprice")
    private Long gasPrice;

    @Column(name="gaslimit")
    private Long gasLimit;

    @Column(name="nonce")
    private Long nonce;

    @Column(name = "timestamp")
    private Date timeStamp;
        
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "value")
    private BigDecimal value;
    
    @Column(name = "operator")
    private String operator;
    
    @Column(name = "owner")
    private String owner;
    
    @Column(name = "tokenname")
    private String tokenname;
    
    @Column(name = "symbol")
    private String symbol;
    
    @Column(name = "decimals")
    private Integer decimals;
    
    @Column(name = "totalsupply")
    private BigDecimal totalsupply;
}
