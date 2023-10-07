package com.imooc.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.Blocks;
import com.imooc.pojo.StatGlobal;
import com.imooc.pojo.Transaction;
import com.imooc.utils.MyMapper;


public interface StatisticMapper extends MyMapper<StatGlobal> {

	StatGlobal getGlobalStat();
	
	List<Blocks> getIndexBlock();
	
	List<Transaction> getIndexTransaction();
	
	Long getTransactionCount();
	
	
	StatGlobal statGlobalData();
	
	void saveGlobalStat(@Param("data") StatGlobal data);
		
	void updateGlobalReward(@Param("blocknumber") Long blocknumber);
	void updateGlobalRelease(@Param("lease_his") BigDecimal totalSpace);
}
