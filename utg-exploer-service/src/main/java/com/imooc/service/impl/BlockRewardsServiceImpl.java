package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.BlockMapper;
import com.imooc.mapper.BlockRewardsMapper;
import com.imooc.pojo.BlockRewards;
import com.imooc.pojo.Blocks;
import com.imooc.pojo.Form.BlockRewardQueryForm;
import com.imooc.service.BlockRewardsService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.ResultMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BlockRewardsServiceImpl implements BlockRewardsService {
    @Autowired
    private BlockRewardsMapper blockRewardsMapper;
    @Autowired
    private BlockMapper blockMapper;
    private static final Logger logger = LoggerFactory.getLogger(BlockRewardsServiceImpl.class);
    public ResultMap getBlockRewardByAddress(BlockRewardQueryForm blockRewardQueryForm) {
        Page page =blockRewardQueryForm.newFormPage();
        long pageSize =page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        String address=blockRewardQueryForm.getAddress();
        List<BlockRewards> list= blockRewardsMapper.getRewadInfoByAddress(address,pageSize,pageCurrent);
        return ResultMap.getSuccessfulResult(list);
    }

    @Override
    public ResultMap getRewardByBlockNumber(long blockNumber) {
        List<BlockRewards> listInfo =blockRewardsMapper.getBlockRewardByBlockNo(blockNumber);
        return ResultMap.getSuccessfulResult(listInfo);
    }

    @Override
    public ResultMap getRewardByTimeStamp(long timeStamp, String value) {
        long times=timeStamp*1000;
        String timeStp= DateUtil.stampToDate(times);
        if(value.equals("before")){
            List<Blocks>  listInfo =blockMapper.getBeforeBlocks(timeStp);
            return ResultMap.getSuccessfulResult(listInfo);
        }else{
            List<Blocks>  listInfo =blockMapper.getAfterBlocks(timeStp);
            return ResultMap.getSuccessfulResult(listInfo);
        }
    }

    @Override
    public void saveBlockReward(BlockRewards reward) {

    }
}
