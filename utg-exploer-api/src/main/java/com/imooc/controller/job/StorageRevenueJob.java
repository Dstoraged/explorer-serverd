package com.imooc.controller.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.job.service.StorageService;

public class StorageRevenueJob extends AgentSvcTask {

	private static Logger logger = LoggerFactory.getLogger(StorageRevenueJob.class);    
	private Long blockNumber;
	private Web3j web3j;
	private StorageService storageService;


    public StorageRevenueJob(Web3j web3j, Long blockNumber, StorageService storageService) {
        this.web3j = web3j;
        this.blockNumber = blockNumber;
        this.storageService = storageService;   
    }

    protected  void runTask() {
        long starttime = System.currentTimeMillis();
        logger.info("StorageRevenueJob start .....");
        try {
        //	updateStorageRevenue(blockNumber, web3j, storageService);
        	storageService.updateStorageRevenue(blockNumber);
						
        }catch (Exception e) {
            logger.error("StorageRevenueJob error,"+e.getMessage(),e);
            return;
        }
        long endtime = System.currentTimeMillis();
        logger.info("StorageRevenueJob end ,spends total second ="+(endtime-starttime)/1000);
    }
    	
}
