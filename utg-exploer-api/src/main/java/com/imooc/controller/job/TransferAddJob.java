package com.imooc.controller.job;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.Utils.TransferAddUtil;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.mapper.TransferMinerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * There is one record for the release of data
 * @author zhc 2021-11-18 13:56
 */
public class TransferAddJob extends AgentSvcTask {
    private static Logger logger = LoggerFactory.getLogger(TransferAddJob.class);
    private UtgStorageMinerMapper UtgStorageMinerMapper;
    private TransferMinerMapper transferMinerMapper;
    private Long blockNumber;
    private Date yesterday;

    public TransferAddJob(Date yesterday, Long blockNumber, UtgStorageMinerMapper UtgStorageMinerMapper, TransferMinerMapper transferMinerMapper) {
        this.yesterday = yesterday;
        this.blockNumber = blockNumber;
        this.UtgStorageMinerMapper = UtgStorageMinerMapper;
        this.transferMinerMapper = transferMinerMapper;
    }

    protected  void runTask() {
        long starttime = System.currentTimeMillis();
        logger.info("TransferAddJob start .....");
        try {
            TransferAddUtil t = new TransferAddUtil();
            t.transferAdd(transferMinerMapper,blockNumber,0);
            new NetStaticsJob(UtgStorageMinerMapper,transferMinerMapper,yesterday).start();
        }catch (Exception e) {
            logger.error("TransferAddJob error,"+e.getMessage(),e);
        }
        long endtime = System.currentTimeMillis();
        logger.info("TransferAddJob end ,spends total second ="+(endtime-starttime)/1000);
    }


}
