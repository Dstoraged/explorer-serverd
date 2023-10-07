package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.NodeReward;
import com.imooc.pojo.Form.NodeLjQueryForm;
import com.imooc.utils.MyMapper;

public interface NodeLjRewardMapper  extends   MyMapper<NodeReward>{
	List<NodeReward> getPageList(@Param("ljForm") NodeLjQueryForm ljForm);
	 long getTotal(@Param("ljForm") NodeLjQueryForm ljForm);
	 void insertNodeLjReward(@Param("rewardList")List<NodeReward> list);
	 void updateNodeLjReward(@Param("list")List<NodeReward> list);
	List<NodeReward>  getNodeRewardByBlock(@Param("blockNumber") Long blockNumber);
	List<NodeReward>   getNodeRewardStat(@Param("blockNumber") Long blockNumber);
}
