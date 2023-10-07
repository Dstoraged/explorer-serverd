package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.ContractLockup;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.utils.MyMapper;

public interface ContractLockupMapper extends MyMapper<ContractLockup> {
	
	List<ContractLockup> getPageList(@Param("blockQueryForm") BlockQueryForm blockQueryForm);
	
	long getTotal(@Param("blockQueryForm") BlockQueryForm blockQueryForm);
	
	
	List<ContractLockup> getList(@Param("contract") String contract, @Param("type") Integer type, @Param("lockupnumber") Long lockupnumber);
	
	int saveOrUpdate(@Param("item") ContractLockup contractLockup);
		
	void updateCancel(@Param("item") ContractLockup contractLockup);

	void insertBatch(@Param("list") List<ContractLockup> list);

	void updateCancelBatch(@Param("list") List<ContractLockup> list);
}
