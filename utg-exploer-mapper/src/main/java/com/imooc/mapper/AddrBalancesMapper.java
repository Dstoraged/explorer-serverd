package com.imooc.mapper;

import com.imooc.pojo.AddrBalances;
import com.imooc.utils.MyMapper;
import com.imooc.utils.ResultMap;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AddrBalancesMapper  extends MyMapper<AddrBalances> {
    List<AddrBalances> getForAddressBalance(@Param("addressHash")String addressHash);

    BigDecimal getBalance(@Param("fromaddr")String fromAddr,@Param("blocknumber") Long blockNumber);

    void saveOrUpdate(@Param("item")AddrBalances item);


}
