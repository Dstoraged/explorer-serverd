package com.imooc.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class GlobalConfig {

	private String type;
	private String value;
	private String txhash;
	private Long blocknumber;
	private Date updatetime;
}
