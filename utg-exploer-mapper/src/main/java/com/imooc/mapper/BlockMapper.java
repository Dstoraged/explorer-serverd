package com.imooc.mapper;

import com.imooc.pojo.Blocks;
import com.imooc.pojo.Form.DposVoterQueryForm;
import com.imooc.pojo.vo.BlockRewardVo;
import com.imooc.pojo.vo.BlocksVo;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface BlockMapper  extends MyMapper<Blocks> {
    List<Blocks> getBeforeBlocks(@Param("timeStp")String timeStp);

    List<Blocks> getAfterBlocks(@Param("timeStp")String timeStp);

    Long getLeatestBlockNumber();

    List<BlocksVo> pageList(@Param("pageCurrent")Long pageCurrent, @Param("pageSize") Long pageSize, @Param("startNumber")Long startNumber,
                            @Param("address")String address,@Param("mineraddress")String mineraddress,@Param("burnflag") Long burnflag,@Param("blockNumber") Long minNumber);

    long getTotalCount(@Param("address")String address,@Param("mineraddress")String mineraddress,@Param("burnflag")Long isBurnRecord,@Param("blockNumber") Long minNumber);
    BigDecimal getBlocksGasBurn(@Param("blockNumber")Long blockNumber);
    List<BlocksVo> getBlockInfoByNumber(@Param("blockNumber")Long blockNumber);

    Blocks getminBlock(@Param("blockNumber")long blockNumber);

    long getCountByThisYear(@Param("dateTime")String dateTime);

    void insertOrUpdate(@Param("accounts")Blocks item);

    List<BlockRewardVo> getAllRewardForaddress(@Param("dposVoterQueryForm")DposVoterQueryForm dposVoterQueryForm);

    long getTotalRewardCount(@Param("dposVoterQueryForm")DposVoterQueryForm dposVoterQueryForm);
    
    
    Long getTimeBeforBlockNumber(@Param("timestamp") Date timestamp);
    
    Long getTimeAfterBlockNumber(@Param("timestamp") Date timestamp);
    
    
    List<Blocks> getBlocksAfter(@Param("blockNumber")Long blockNumber);        
    Long truncBlock(@Param("blockNumber")Long blockNumber);
	Long truncBlockFork(@Param("blockNumber")Long blockNumber);
	Long truncBlockRewards(@Param("blockNumber")Long blockNumber);
	Long truncPunished(@Param("blockNumber")Long blockNumber);

	Long truncTransaction(@Param("blockNumber")Long blockNumber);
	Long truncContract(@Param("blockNumber")Long blockNumber);
			
	Long truncStorageSpace(@Param("blockNumber")Long blockNumber);
	Long truncStorageRent(@Param("blockNumber")Long blockNumber);
	Long truncStorageRequest(@Param("blockNumber")Long blockNumber);
}
