package com.imooc.pojo;

import com.imooc.form.BasePo;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
public class Tokens extends BasePo {

    @Column(name="contract")
    private String  contract;

    @Column(name="type")
    private Integer  type;

    @Column(name="name")
    private String name;

    @Column(name="symbol")
    private String symbol;

    @Column(name="decimals")
    private Integer decimals;

    @Column(name="totalsupply")
    private BigDecimal totalSupply;

    @Column(name="cataloged")
    private Integer  cataloged;

    @Column(name="description")
    private String description;

    @Column(name="contractmanager")
    private String contractManager;

    private Long accountTotal;
    
    private BigDecimal balance;
	private Long txcount;
}

