package com.imooc.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.NodeReward;
import com.imooc.pojo.Form.NodeLjQueryForm;
import com.imooc.utils.ResultMap;

public interface NodeLjRewardService {
	 ResultMap<Page<NodeReward>> pageList(NodeLjQueryForm ljForm);
	 void insertNodeLjReward(List<NodeReward> list);
	 void updateNodeLjReward(List<NodeReward> list);
	 public HashMap<String,Long> getExitRewardByBlock(long blockNumber);
}
