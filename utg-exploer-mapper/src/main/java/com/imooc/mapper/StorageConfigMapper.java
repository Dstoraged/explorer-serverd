package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.StorageConfig;
import com.imooc.utils.MyMapper;

public interface StorageConfigMapper extends MyMapper<StorageConfig> {

//  void insertOrUpdate(@Param("config")StorageConfig config);
  
//	int insertConfig(StorageConfig config);
  
	List<StorageConfig> getList(@Param("type") String type);
	
	StorageConfig getConfig(@Param("type") String type,@Param("seq") Integer seq);
  
}
