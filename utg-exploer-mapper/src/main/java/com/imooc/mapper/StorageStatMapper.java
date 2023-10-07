package com.imooc.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.StorageGlobalStat;
import com.imooc.pojo.StorageSpaceStat;
import com.imooc.utils.MyMapper;

public interface StorageStatMapper extends MyMapper<StorageSpaceStat> {

	void insertSpaceStatBatch(@Param("list") List<StorageSpaceStat> list);

	void saveSpaceStat(@Param("blocknumber") Long blocknumber, @Param("blocktime") Date blocktime, @Param("sttime") Date sttime);
	
	void updateSpaceRewardStat(@Param("blocknumber") Long blocknumber);

	void saveRevenueStat(@Param("blocknumber") long blocknumber, @Param("blocktime") Date blocktime, @Param("sttime") Date sttime);
	
//	void saveRevenueStat(Map<String,Object> params);
	
	void saveGlobalStat(@Param("blocknumber") long blocknumber, @Param("blocktime") Date blocktime, @Param("sttime") Date sttime);
	
//	void saveGlobalStat(Map<String,Object> params);
	
	List<StorageGlobalStat> queryStorageGlobalStat(@Param("startTime") String startTime,@Param("endTime") String endTime);
	
}
