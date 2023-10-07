package com.imooc.pojo;

import javax.persistence.Column;

import lombok.Data;

@Data
public class ContractVersion{

	@Column(name="version")
    private String version;
	
	@Column(name="vername")
    private String vername;
	
	@Column(name="metadata_bytecode")
    private String metadata_bytecode;
	
	@Column(name="status")
    private Integer status;
	
	@Column(name="ord")
    private Integer ord;
	
}