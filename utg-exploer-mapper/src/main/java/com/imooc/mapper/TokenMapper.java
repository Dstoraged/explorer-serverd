package com.imooc.mapper;

import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.TokenQueryForm;
import com.imooc.pojo.Tokens;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface  TokenMapper extends MyMapper<Tokens> {
    List<Tokens> getTokenSupply(@Param("contractAddress")String contractAddress);

    Tokens getTokens(@Param("contract")String contract);
    
    List<Tokens> getAddressTokenList(@Param("address")String address);

    void saveOrUpdate(@Param("item")Tokens item);

    List<Tokens> getTokenListInfo(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    long getTotalTokens(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    long getCountForExit(@Param("tokenQueryForm")TokenQueryForm tokenQueryForm);
}
