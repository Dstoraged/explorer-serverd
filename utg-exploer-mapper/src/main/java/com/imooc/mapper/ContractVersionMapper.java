package com.imooc.mapper;

import java.util.List;

import com.imooc.pojo.ContractVersion;
import com.imooc.utils.MyMapper;

public interface ContractVersionMapper extends MyMapper<ContractVersion> {

	List<ContractVersion> getContractVersionList();
	
	ContractVersion getContractVersion(String version);
}
