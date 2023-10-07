package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.ContractSource;
import com.imooc.utils.MyMapper;

public interface ContractSourceMapper extends MyMapper<ContractSource> {

//	List<ContractSource> getPageList(@Param("blockQueryForm") BlockQueryForm blockQueryForm);
	
//	long getTotal(@Param("blockQueryForm") BlockQueryForm blockQueryForm);
	
	List<ContractSource> getContractSources(String contract);
	
	int saveOrUpdate(@Param("data") ContractSource contractSource);
}
