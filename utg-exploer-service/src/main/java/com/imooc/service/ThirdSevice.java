package com.imooc.service;

import com.imooc.utils.ResultMap;

public interface ThirdSevice {

	ResultMap<?> getRevenue(String address);
	
	ResultMap<?> getRewards(String address,Integer type,Long startblock,Long endblock,Long datetime);
	
	ResultMap<?> getTotalRewards(String address,Integer type);
	
	ResultMap<?> getReleases(String address,Integer type);
	
	
	ResultMap<?> getPosNodes(Integer type,String address);
	
	ResultMap<?> getPosNodePledge(String address,Integer status);
}
