package com.imooc.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import lombok.Data;

@Data
public class Contract {

	@Column(name = "contract")
	private String contract;
	
	@Column(name = "contractname")
	private String contractname;

	@Column(name = "name")
	private String name;

	@Column(name = "symbol")
	private String symbol;
	
	@Column(name = "txhash")
	private String txhash;
	
	@Column(name = "author")
	private String author;
	
	@Column(name = "blocknumber")
	private Long blocknumber;
	
	@Column(name = "verified")
	private Integer verified;
	
	@Column(name = "version")
	private String version;
	
	@Column(name = "verifydate")
	private Date verifydate;	
	
	@Column(name = "istoken")
	private Integer istoken;	
	
	@Column(name = "type")
	private String type;

	@Column(name = "admin1")
	private String admin1;
	
	@Column(name = "admin2")
	private String admin2;
	
	@Column(name = "lockupstart")
	private Long lockupstart;
	
	@Column(name = "lockupperiod")
	private Long lockupperiod;
	
	@Column(name = "releaseperiod")
	private Long releaseperiod;
	
	@Column(name = "releaseinterval")
	private Long releaseinterval;
	
	@Column(name = "createtime")
	private Date createtime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	private BigDecimal balance;
	private Long txcount;
	private String vername;
	private String input;
	
}
