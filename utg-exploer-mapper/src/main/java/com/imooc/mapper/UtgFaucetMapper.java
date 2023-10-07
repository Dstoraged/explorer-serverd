package com.imooc.mapper;

import com.imooc.pojo.UtgFaucet;
import com.imooc.pojo.vo.AddressVo;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface UtgFaucetMapper extends MyMapper<UtgFaucet> {

    UtgFaucet getFaucetByAddress(@Param("address")String address);
}
