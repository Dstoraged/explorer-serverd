package com.imooc.mapper;

import com.imooc.pojo.Contracts;
import com.imooc.pojo.Transaction;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface ContractsMapper extends MyMapper<Contracts> {
    void saveOrUpdate(@Param("item")Contracts item);
}
