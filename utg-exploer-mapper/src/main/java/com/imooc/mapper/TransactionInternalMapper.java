package com.imooc.mapper;

import com.imooc.pojo.Transinternal;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TransactionInternalMapper {
    List<Transinternal> getTernalListInfo(@Param("paramsMap")Map<String, Object> map);

    List<Transinternal> getTernalByTransactionHash(@Param("txHash")String txHash);

    List<Transinternal> getTernalByBlock(@Param("paramsMap")Map<String, Object> map);
}
