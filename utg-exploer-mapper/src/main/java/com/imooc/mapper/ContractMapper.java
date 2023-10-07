package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.Contract;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.utils.MyMapper;

public interface ContractMapper extends MyMapper<Contract> {
	
	Contract getContract(@Param("contract")String contract);

    void saveOrUpdate(@Param("contract")Contract contract);
    
    void updateVerify(@Param("contract")Contract contract);

    List<Contract> getPageList(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    long getTotal(@Param("blockQueryForm")BlockQueryForm blockQueryForm);
    
    List<Contract> getAuthorizedContracts(@Param("address") String address);
    
    List<Contract> getAuthorizedContracts2(@Param("address") String address);
    
    Contract getAuthorizedContract(@Param("contract")String contract,@Param("address") String address);
}
