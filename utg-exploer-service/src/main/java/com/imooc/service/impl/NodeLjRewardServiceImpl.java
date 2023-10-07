package com.imooc.service.impl;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.NodeLjRewardMapper;
import com.imooc.pojo.NodeReward;
import com.imooc.pojo.Form.NodeLjQueryForm;
import com.imooc.service.NodeLjRewardService;
import com.imooc.utils.ResultMap;
@Service
public class NodeLjRewardServiceImpl implements NodeLjRewardService{
protected static Logger logger = LoggerFactory.getLogger(NodeLjRewardServiceImpl.class);
	
	@Autowired
	private NodeLjRewardMapper ljRdDao;
	@Override
	public ResultMap<Page<NodeReward>> pageList(NodeLjQueryForm ljForm) {
		 Page page = ljForm.newFormPage();
			Long pageSize = page.getSize();
			Long pageCurrent = page.getCurrent();
			pageCurrent = (pageCurrent - 1) * pageSize;
			ljForm.setCurrent(pageCurrent);
			List<NodeReward> list = ljRdDao.getPageList(ljForm);
			long total = ljRdDao.getTotal(ljForm);
			page.setRecords(list);
			page.setTotal(total);
			return ResultMap.getSuccessfulResult(page);
	 }
	@Override
	public void insertNodeLjReward(List<NodeReward> list) {
		ljRdDao.insertNodeLjReward(list);
		
	}
	public HashMap<String,Long> getExitRewardByBlock(long blockNumber){
		HashMap<String,Long>  map=new HashMap<>();
		List<NodeReward> list=ljRdDao.getNodeRewardByBlock(blockNumber);
		if(list!=null) {
			for(NodeReward info:list) {
				map.put(info.getTargetAddress(), info.getRewardId());
			}
		}
		return map;
	}
	@Override
	public void updateNodeLjReward(List<NodeReward> list) {
		ljRdDao.updateNodeLjReward(list);
		
	}
}
