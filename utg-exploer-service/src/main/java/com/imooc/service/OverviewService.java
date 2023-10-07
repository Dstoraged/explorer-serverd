package com.imooc.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.StatisticMapper;
import com.imooc.pojo.Blocks;
import com.imooc.pojo.StatGlobal;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.vo.BlocksVo;
import com.imooc.utils.ResultMap;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;

@Service
@SuppressWarnings({"rawtypes","unchecked"})
public class OverviewService {

	@Autowired
	private StatisticMapper statisticMapper;
	@Autowired
	private StorageService storageService;
	private TimedCache<String, ResultMap> cache = CacheUtil.newTimedCache(60 * 1000L);
	
	private ResultMap<Page<BlocksVo>> blockList;
	private ResultMap<Page<Transaction>> transactionList;
	
	public ResultMap<?> getDatas() {
		StatGlobal stat = statisticMapper.getGlobalStat();
		Map<String,Object> data = new HashMap<>();
		data.put("totalBlockNumber", stat.getBlocknumber());
	//	data.put("totalTxCount", stat.getTx_count());
		data.put("destrNum", stat.getTotal_burnt());
		data.put("pledgeNum", stat.getTotal_pledge());
		data.put("bandWidthSize", stat.getTotal_storage());
		data.put("lockNum", stat.getTotal_locked());
		data.put("utgToGb", stat.getStorage_amount());
		data.put("utg24", stat.getLast_mintage());
		data.put("nextElectTime", stat.getNext_election());
		BigDecimal rate= storageService.getGbUtgRate(stat.getLease_his());
		if(rate!=null&&rate.compareTo(BigDecimal.ZERO)!=0){
			rate=BigDecimal.valueOf(10).pow(18).divide(rate, 4, BigDecimal.ROUND_HALF_DOWN);
		}
		data.put("gbutgRate",rate);
		return ResultMap.getSuccessfulResult(data);
	}
	
	private synchronized ResultMap<Page<BlocksVo>> loadBlockCache() {	
//		BlockQueryForm blockQueryForm = new BlockQueryForm();
//		blockQueryForm.setCurrent(1);
//		blockQueryForm.setSize(5);		
//		blockList = blockService.pageList(blockQueryForm);	
		Page<Blocks> page = new Page(1L, 5L);
		List<Blocks> records = statisticMapper.getIndexBlock();
		StatGlobal data = statisticMapper.getGlobalStat();
		page.setRecords(records);
		page.setTotal(data.getBlocknumber());
		blockList = ResultMap.getSuccessfulResult(page);
		cache.put("block", blockList);
		return blockList;
	}
	
	public ResultMap<Page<BlocksVo>> getIndexBlock(){
		blockList=cache.get("block", false);		
		if(blockList==null){
			blockList=loadBlockCache();
		}
		return blockList;
	}
	
	private synchronized ResultMap<Page<Transaction>> loadTransactionCache() {		
//		BlockQueryForm blockQueryForm = new BlockQueryForm();
//		blockQueryForm.setCurrent(1);
//		blockQueryForm.setSize(5);			
//		transactionList = transactionService.pageList(blockQueryForm);		
		Page<Transaction> page = new Page(1L, 5L);
		List<Transaction> records = statisticMapper.getIndexTransaction();
		Long count = statisticMapper.getTransactionCount();
		page.setRecords(records);
		page.setTotal(count);
		transactionList = ResultMap.getSuccessfulResult(page);
		cache.put("transaction", transactionList);
		return transactionList;			
	}
	
	public ResultMap<Page<Transaction>> getIndexTransaction(){
		transactionList= cache.get("transaction", false);
		if(transactionList==null){
			transactionList=loadTransactionCache();
		}
		return transactionList;
	}
	
	public ResultMap<?> getIndexData(){
		StatGlobal stat = statisticMapper.getGlobalStat();
		return ResultMap.getSuccessfulResult(stat);	
	}
	
	public ResultMap<?> getAddr0Data(Long blockNumber) {
		StatGlobal stat = statisticMapper.getGlobalStat();
		Map<String,Object> data = new HashMap<>();
		data.put("balance", stat.getTotal_burnt());
		data.put("txin", stat.getTx_burnt());
		data.put("burned", stat.getReward_burnt());
		data.put("blockBurn", stat.getFee_burnt());
		data.put("pledgeBurn", stat.getPledge_burnt());
		BigDecimal other = stat.getTotal_burnt().subtract(stat.getTx_burnt()).subtract(stat.getPledge_burnt()).subtract(stat.getPledge_burnt())
				.subtract(stat.getReward_burnt());
		data.put("nontx", other);
		return ResultMap.getSuccessfulResult(data);		
	}
}
