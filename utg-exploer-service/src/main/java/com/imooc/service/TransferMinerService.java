package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Form.*;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.TransferMiner;
import com.imooc.pojo.vo.TransferMinerVo;
import com.imooc.utils.ResultMap;

import java.util.List;
import java.util.Map;

public interface TransferMinerService {
    ResultMap<TransferMiner> getMinerInfoForAddress(TransferMinerForm transferMinerForm);

    ResultMap getAllMinerInfoWallet(MinerQueryForm minerQueryForm);

    ResultMap <Page<TransferMiner>>getLockAndPledgeInfo(LockUpTransferForm lockUpTransferForm);

    ResultMap<Page<TransferMinerVo>> getLockUtgMinerInfo(BlockQueryForm blockQueryForm) throws Exception;

    ResultMap getUtgMinerLockSum(BlockQueryForm blockQueryForm) throws Exception;

    ResultMap getLockSummaryByRevAddress(BlockQueryForm blockQueryForm) throws Exception;

    ResultMap<Page<TransferMinerVo>> getlockUtgMinersByRevAddress(BlockQueryForm blockQueryForm) throws Exception;

    ResultMap<Page<TransferMiner>> getOverData(BlockQueryForm blockQueryForm);

    ResultMap getPledgeExit()throws Exception;

    ResultMap<Page<Transaction>> getAllTotalForReceive(String address);

    ResultMap<Page<TransferMinerVo>> getLockUtgMinerInfoWallet(BlockQueryForm blockQueryForm)throws Exception;



    ResultMap<Page<TransferMinerVo>> getRewardList(BlockQueryForm blockQueryForm);
	
	ResultMap<Map> getRewardStat(BlockQueryForm blockQueryForm);
	
	ResultMap<TransferMinerVo> getRewardInfo(String id);	
    
}
