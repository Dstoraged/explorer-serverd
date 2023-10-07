package com.imooc.mapper;

import com.imooc.pojo.TransLogs;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LogMapper  extends MyMapper<TransLogs> {
    List<TransLogs> getLogs(@Param("address") String address,@Param("topic") String topic);

    void saveOrUpdate(@Param("tranlog")TransLogs transLog);
}
