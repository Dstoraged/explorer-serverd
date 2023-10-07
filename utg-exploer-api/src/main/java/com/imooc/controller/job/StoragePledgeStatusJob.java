package com.imooc.controller.job;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.job.service.StorageService;

public class StoragePledgeStatusJob extends AgentSvcTask{
	
	private static Logger logger = LoggerFactory.getLogger(StoragePledgeStatusJob.class);
	private Long blockNumber;
	private Long dayOneNumber;
	private Web3j web3j;
	private StorageService storageService;
    private Map<Long,BigInteger> blockTimeMap;
    public StoragePledgeStatusJob(Web3j web3j, Long blockNumber, Long dayOneNumber, StorageService storageService,Map<Long,BigInteger> blockTimeMap) {
    	this.web3j = web3j;
        this.blockNumber = blockNumber;
        this.dayOneNumber = dayOneNumber;
        this.storageService = storageService;
        this.blockTimeMap=blockTimeMap;
        if(this.blockTimeMap==null) {
        	this.blockTimeMap=new HashMap<Long, BigInteger>();
        }
    }
    
	protected  void runTask() {
        long starttime = System.currentTimeMillis();
        logger.info("StoragePledgeStatusJob start .....");
        
    //    updateStoragePledgeStatus(blockNumber,dayOneNumber,web3j,storageService);        
    //    StorageDailyJob.updateStorageAmount(storageService);
        
        storageService.updateStoragePledgeStatus(blockNumber,blockTimeMap);
		storageService.updateStorageVaildProgress(blockNumber);
	//	storageService.updateStorageAmount(blockNumber);
        long endtime = System.currentTimeMillis();
        logger.info("StoragePledgeStatusJob end ,spends total second ="+(endtime-starttime)/1000);
    }
	
}
