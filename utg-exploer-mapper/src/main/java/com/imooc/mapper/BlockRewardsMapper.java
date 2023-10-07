package com.imooc.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.BlockFork;
import com.imooc.pojo.BlockRewards;
import com.imooc.utils.MyMapper;
import com.imooc.utils.ResultMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**/
public interface BlockRewardsMapper extends MyMapper<BlockRewards> {
    List<BlockRewards> getRewadInfoByAddress(@Param("address")String address, @Param("pageSize")long pageSize, @Param("pageCurrent") Long pageCurrent);

    List<BlockRewards> getBlockRewardByBlockNo(@Param("blockNumber")long blockNumber);

    void insertOrUpdate(@Param("reward")BlockRewards reward);
}
