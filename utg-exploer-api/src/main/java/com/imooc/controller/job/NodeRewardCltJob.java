package com.imooc.controller.job;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.imooc.Utils.AgentSvcTask;
import com.imooc.pojo.NodeReward;
import com.imooc.service.NodeLjRewardService;
import com.imooc.utils.Constants;
import com.imooc.utils.HttpUtils;

/**
 * @author 
 *
 */
public class NodeRewardCltJob extends AgentSvcTask{
	private static Logger logger = LoggerFactory.getLogger(NodeRewardCltJob.class);
    private long blockNumber;
    private String url;
    private long dayOneNumber;
    private Date blockDate=null;
    private boolean isJIsuan=false;
    private NodeLjRewardService ljRewardService;
	public NodeRewardCltJob(String url, long blockNumber
			,long dayOneNumber,BigInteger timestamp,NodeLjRewardService ljRewardService) {
		this.url=url;
		this.blockNumber=blockNumber;
		this.dayOneNumber=dayOneNumber;
		this.ljRewardService=ljRewardService;
		if((this.blockNumber-this.dayOneNumber-1)%(this.dayOneNumber*3)==729) {
			isJIsuan=true;
		}
		if(timestamp!=null) {
			blockDate=new Date(timestamp.longValue()*1000);
		}
	}
	protected void runTask() {
		try {
			 logger.info("NodeRewardCltJob "+blockNumber+" start .....");
			HashMap<String,BigDecimal>  beforeWkRdMap=cltNodeBeforWkLjReward(url ,blockNumber-1-dayOneNumber);
			if(beforeWkRdMap!=null) {
				cltNodeLjReward(url,blockNumber-1,beforeWkRdMap);
			}
			
			
		}catch (Exception e) {
			logger.warn("NodeReward clt error",e);
		}
	}
	public void cltNodeLjReward(String url,long cltNumber,HashMap<String,BigDecimal>  beforeWkRdMap) {
		String param="{\"jsonrpc\":\"2.0\",\"method\":\"alien_getSnapshotRewardBalanceV1\",\"params\":["+cltNumber+",\"\"],\"id\":1}";
		String rewardData=HttpUtils.HttpPostWithJson(url,param);
		if (rewardData != null && !rewardData.equals("0.0")) {
			JSONObject spJson = JSONObject.parseObject(rewardData);
			JSONObject result = spJson.getJSONObject("result");
			if (result != null) {
				JSONObject	flowrevenve=result.getJSONObject("flowrevenve");
				List<NodeReward>  rewardList=new ArrayList<>();
				List<NodeReward>  upRewardList=new ArrayList<>();
				if(flowrevenve!=null) {
					HashMap<String,Long> checkMap=ljRewardService.getExitRewardByBlock(cltNumber);			    	
					for(String target:flowrevenve.keySet()) {
						JSONObject targetObj=flowrevenve.getJSONObject(target);
						if(targetObj!=null) {
							JSONObject rewardbalanceV1 =targetObj.getJSONObject("rewardbalanceV1");
						    if(rewardbalanceV1!=null) {
						    	for( String rewardType  :rewardbalanceV1.keySet()){
						    		JSONObject reward=rewardbalanceV1.getJSONObject(rewardType);
						    		if(reward!=null) {
						    			for(String nodeAddress:reward.keySet()) {
						    				JSONObject  detail=	reward.getJSONObject(nodeAddress);
						    				NodeReward info=new NodeReward();
											info.setTargetAddress(Constants.prefixAddress(target));
											info.setBlockNumber(blockNumber);
											info.setRewardType(Integer.parseInt(rewardType));
											if("3".equals(rewardType)||"6".equals(rewardType)) {
												info.setNodeType("PoS");
											}else if ("4".equals(rewardType) ||"5".equals(rewardType)||"13".equals(rewardType)) {
												info.setNodeType("SN");
											}else if ("8".equals(rewardType) ||"9".equals(rewardType)) {
												info.setNodeType("SP");
											}
											
											info.setLeijiAmount(detail.getBigDecimal("Amount"));
											String key=target+"_"+nodeAddress+"_"+rewardType;
											BigDecimal lastAmount=beforeWkRdMap==null?BigDecimal.ZERO: beforeWkRdMap.get(key);
											lastAmount=lastAmount==null?BigDecimal.ZERO:lastAmount;
											info.setRewardAmount(info.getLeijiAmount().subtract(lastAmount));
											if(info.getRewardAmount().compareTo(BigDecimal.ZERO)<0) {
												info.setRewardAmount(info.getLeijiAmount());
											}
											if(info.getRewardAmount().compareTo(BigDecimal.ZERO)==0) {
												continue;
											}
											info.setRevenueAddress(Constants.prefixAddress(detail.getString("RevenueAddress")));
											info.setNodeAddress(Constants.prefixAddress(nodeAddress));
											info.setRewardTime(blockDate==null?new Date():blockDate);
						    			    info.setInstime(new Date());
						    			  //target_address,node_address,reward_type,node_type
											String dbKey=info.getTargetAddress()+info.getNodeAddress()+info.getRewardType()+info.getNodeType();
						    			   Long rewardId= checkMap.get(dbKey);
						    			   if(rewardId!=null&&rewardId>0) {
						    				   info.setRewardId(rewardId);
						    				   upRewardList.add(info);
						    			   }else {
						    				   rewardList.add(info);
						    			   }
						    			   if(upRewardList.size()>100) {
						    					ljRewardService.updateNodeLjReward(rewardList);
						    					rewardList.clear();
						    			    }
						    			    if(rewardList.size()>100) {
						    					ljRewardService.insertNodeLjReward(rewardList);
						    					rewardList.clear();
						    			    }
						    			}
						    		}
						    	}
						    }
						}
						
					}
				}
				if(rewardList.size()>0) {
					ljRewardService.insertNodeLjReward(rewardList);
				}
			}
		}
	}
	public HashMap<String,BigDecimal> cltNodeBeforWkLjReward(String url,long cltNumber) {
		HashMap<String,BigDecimal>  beforeWkRdMap=new HashMap<>();
		if(cltNumber<=0 ||isJIsuan) {
			return beforeWkRdMap;
		}
		try {
		String param="{\"jsonrpc\":\"2.0\",\"method\":\"alien_getSnapshotRewardBalanceV1\",\"params\":["+cltNumber+",\"\"],\"id\":1}";
		String rewardData=HttpUtils.HttpPostWithJson(url,param);
		if (rewardData != null && !rewardData.equals("0.0")) {
			JSONObject spJson = JSONObject.parseObject(rewardData);
			JSONObject result = spJson.getJSONObject("result");
			if (result != null) {
				JSONObject	flowrevenve=result.getJSONObject("flowrevenve");
				if(flowrevenve!=null) {
					for(String target:flowrevenve.keySet()) {
						JSONObject targetObj=flowrevenve.getJSONObject(target);
						if(targetObj!=null) {
							JSONObject rewardbalanceV1 =targetObj.getJSONObject("rewardbalanceV1");
						    if(rewardbalanceV1!=null) {
						    	for( String rewardType  :rewardbalanceV1.keySet()){
						    		JSONObject reward=rewardbalanceV1.getJSONObject(rewardType);
						    		if(reward!=null) {
						    			for(String nodeAddress:reward.keySet()) {
						    				JSONObject  detail=	reward.getJSONObject(nodeAddress);
						    				String key=target+"_"+nodeAddress+"_"+rewardType;
						    				beforeWkRdMap.put(key, detail.getBigDecimal("Amount"));
						    			}
						    		}
						    	}
						    }
						}
					}
				}
			}
		}
		}catch (Exception e) {
			return null;
		}
		return beforeWkRdMap;
	}
}
