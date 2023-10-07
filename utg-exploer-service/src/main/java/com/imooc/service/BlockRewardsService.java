package com.imooc.service;


import com.imooc.pojo.BlockRewards;
import com.imooc.pojo.Form.BlockRewardQueryForm;
import com.imooc.utils.ResultMap;
public interface BlockRewardsService {
    ResultMap getBlockRewardByAddress(BlockRewardQueryForm blockRewardQueryForm);

    ResultMap getRewardByBlockNumber(long blockNumber);

    ResultMap getRewardByTimeStamp(long timeStamp, String value);

    void saveBlockReward(BlockRewards reward);
}
