package com.imooc.mapper;

import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.TokenContract;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractTokenMapper extends MyMapper<TokenContract> {
    List<TokenContract> getTokenContract(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    long getTotal(BlockQueryForm blockQueryForm);

    void insertOrUpdate(@Param("token")TokenContract token);
}
