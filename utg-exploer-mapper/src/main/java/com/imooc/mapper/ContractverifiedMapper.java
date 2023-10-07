package com.imooc.mapper;

import com.imooc.pojo.Contractverified;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractverifiedMapper {
	
    List<Contractverified> getContractInfo(@Param("contractAddress")String address);
}
