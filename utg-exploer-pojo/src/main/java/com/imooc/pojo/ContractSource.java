package com.imooc.pojo;

import javax.persistence.Column;

import lombok.Data;

@Data
public class ContractSource{

	@Column(name="id")
    private Long id;
	
	@Column(name="contract")
    private String contract;
	
	@Column(name="filename")
    private String filename;
	
	@Column(name="sourcecode")
    private String sourcecode;
	
	@Column(name="bin")
	private String bin;
	
	@Column(name="abi")
	private String abi;
	
	@Column(name="ord")
    private Integer ord;

	private String filepath;
}
