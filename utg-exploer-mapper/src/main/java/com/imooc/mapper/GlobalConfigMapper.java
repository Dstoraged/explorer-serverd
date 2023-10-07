package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.GlobalConfig;
import com.imooc.utils.MyMapper;

public interface GlobalConfigMapper extends MyMapper<GlobalConfig> {

	List<GlobalConfig> getList();

	String getTypeValue(@Param("type") String type);

	void saveOrUpdate(@Param("data") GlobalConfig config);

}
