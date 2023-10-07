package com.imooc.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imooc.mapper.ThirdMapper;
import com.imooc.pojo.NodePledge;
import com.imooc.pojo.StorageRent;
import com.imooc.pojo.StorageRevenue;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.TransferMiner;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.service.ThirdSevice;
import com.imooc.utils.Constants;
import com.imooc.utils.ResultMap;

@Service
public class ThirdSeviceImpl implements ThirdSevice{
	
	@Autowired
	ThirdMapper thirdMapper;
	
	@Override
	public ResultMap<?> getRevenue(String revenueaddress) {		
		StorageRevenue revenue = thirdMapper.getRevenueInfo(revenueaddress);
		if (revenue == null) {
			return ResultMap.getFailureResult("Invaild revenue address");
		}
		List<StorageSpace> list = thirdMapper.getRevenueSpaceList(revenueaddress);
		JSONObject json = new JSONObject();
		json.put("address", revenueaddress);
		json.put("stratio", revenue.getRatio());
		JSONArray storages = new JSONArray();
		Map<String, StorageSpace> storageMap = new HashMap<String, StorageSpace>();
		for (StorageSpace space : list) {
			String device_addr = space.getDeviceAddr();
			if (storageMap.containsKey(device_addr))
				continue;
			storageMap.put(device_addr, space);
			int pledgeStatus = space.getPledgeStatus();
			if (pledgeStatus != 0)
				pledgeStatus = 1;
			JSONObject storage = new JSONObject();
			storage.put("address", space.getDeviceAddr());
			storage.put("blocknumber", space.getPledgeNumber());
			storage.put("capacity", space.getDeclareSpace());
			storage.put("bandwidth", space.getBwSize());
		//	storage.put("bwratio", space.getBwRatio());
			storage.put("bwratio", space.getRewardRatio());
			storage.put("pledgeStatus", pledgeStatus);
			storage.put("vaildNumber", space.getValidNumber());
			storage.put("succNumber", space.getSuccNumber());
			storage.put("pledgeAmount", space.getPledgeAmount());
			List<StorageRent> rentList = thirdMapper.getRevenueRentList(device_addr);
			JSONArray leases = new JSONArray();
			for (StorageRent rent : rentList) {
				int rentStatus = rent.getRentStatus();
				int leaseStatus = 1;
				if (rentStatus == 1 || rentStatus == 2 || rentStatus == 3 || rentStatus == 4)
					leaseStatus = 0;
				JSONObject lease = new JSONObject();
				lease.put("hash", rent.getRentHash());
				lease.put("address", rent.getRentAddr());
				lease.put("blocknumber", rent.getRentNumber());
				lease.put("leaseStatus", leaseStatus);
				lease.put("capacity", rent.getRentSpace());
				lease.put("price", rent.getRentPrice());
				lease.put("duration", rent.getRentTime());
				lease.put("vaildNumber", rent.getValidNumber());
				lease.put("succNumber", rent.getSuccNumber());
				lease.put("pledgeAmount", rent.getRentAmount());
				leases.add(lease);
			}
			storage.put("leases", leases);
			storages.add(storage);
		}
		json.put("storages", storages);
		return ResultMap.getSuccessfulResult(json);
	}
	
	private Integer decodeRewardType(Integer type){
		if(type==9)
			return 1;
		else if(type==5)
			return 2;
		else if(type==1)
			return 3;
		else
			return null;
	}
	@Override
	public ResultMap<?> getRewards(String revenueaddress,Integer type,Long startblock,Long endblock,Long datetime) {
		String types = "9,5,1";
		if (type != null && type == 1)
			types = "9";
		else if (type != null && type == 2)
			types = "5";
		else if (type != null && type == 3)
			types = "1";
		List<TransferMiner> list;
		JSONObject json = new JSONObject();
		json.put("address", revenueaddress);		
		if(startblock!=null && endblock!=null){
			if(endblock-startblock>10000)
				return ResultMap.getFailureResult("The difference between start and end block cannot be greater than 10000");
			json.put("startblock", startblock);
			json.put("endblock", endblock);
			list = thirdMapper.getRevenueListBlockrange(startblock, endblock, revenueaddress, types.split(","));
		}else if(datetime!=null){
			Date date = new Date(datetime);
			Calendar ca = Calendar.getInstance();
			ca.setTime(date);
			ca.set(Calendar.HOUR_OF_DAY, 0);
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.SECOND, 0);
			ca.set(Calendar.MILLISECOND, 0);
			Date starttime = ca.getTime();
			ca.add(Calendar.DAY_OF_YEAR, 1);
			Date endtime = ca.getTime();			
			json.put("starttime", starttime.getTime());
			json.put("endtime", endtime.getTime());
			list = thirdMapper.getRevenueListDaterange(starttime, endtime, revenueaddress, types.split(","));
		}else{
			return ResultMap.getFailureResult("Missing required parameter startblock , endblock or datetime.");
		}
		JSONArray rewards = new JSONArray();
		for (TransferMiner tm : list) {
			JSONObject reward = new JSONObject();
			if(tm.getType()==1){
				if(tm.getPledgeAddress()==null||Constants.addressZero.equals(tm.getPledgeAddress()))
					reward.put("address", tm.getAddress());
				else
					reward.put("address", tm.getPledgeAddress());
			}else{				
				reward.put("address", tm.getAddress());
			}
			reward.put("blocknumber", tm.getBlockNumber());
			reward.put("rewardAmount", tm.getTotalAmount());
			reward.put("lockPeriod", tm.getLockNumHeight());
			reward.put("releasePeriod", tm.getReleaseHeigth());
			reward.put("releaseInterval", tm.getReleaseInterval());
			reward.put("releaseAmount", tm.getReleaseamount());
			reward.put("burntAmount", tm.getBurntamount());			
			reward.put("type", decodeRewardType(tm.getType()));			
			rewards.add(reward);
		}
		json.put("rewards", rewards);
		return ResultMap.getSuccessfulResult(json);
	}
		
	@Override
	public ResultMap<?> getTotalRewards(String revenueaddress,Integer type) {
		String types = "9,5,1";
		if (type != null && type == 1)
			types = "9";
		else if (type != null && type == 2)
			types = "5";
		else if (type != null && type == 3)
			types = "1";
		JSONObject json = new JSONObject();
		json.put("address", revenueaddress);
		JSONArray rewards = new JSONArray();
		List<TransferMiner> list = thirdMapper.getReleaseList(revenueaddress, types.split(","));
		for (TransferMiner tm : list) {
			JSONObject release = new JSONObject();
			if(tm.getType()==1){
				if(tm.getPledgeAddress()==null||Constants.addressZero.equals(tm.getPledgeAddress()))
					release.put("address", tm.getAddress());
				else
					release.put("address", tm.getPledgeAddress());
			}else{				
				release.put("address", tm.getAddress());
			}			
			release.put("rewardAmount", tm.getTotalAmount());
			release.put("burntAmount", tm.getBurntamount());
			release.put("type", decodeRewardType(tm.getType()));
			rewards.add(release);
		}
		json.put("rewards", rewards);
		return ResultMap.getSuccessfulResult(json);
	}
	
	@Override
	public ResultMap<?> getReleases(String revenueaddress,Integer type) {
		String types = "9,5,1";
		if (type != null && type == 1)
			types = "9";
		else if (type != null && type == 2)
			types = "5";
		else if (type != null && type == 3)
			types = "1";
		JSONObject json = new JSONObject();
		json.put("address", revenueaddress);
		JSONArray releases = new JSONArray();
		List<TransferMiner> list = thirdMapper.getReleaseList(revenueaddress, types.split(","));
		for (TransferMiner tm : list) {
			JSONObject release = new JSONObject();
			if(tm.getType()==1){
				if(tm.getPledgeAddress()==null||Constants.addressZero.equals(tm.getPledgeAddress()))
					release.put("address", tm.getAddress());
				else
					release.put("address", tm.getPledgeAddress());
			}else{				
				release.put("address", tm.getAddress());
			}			
			release.put("releaseAmount", tm.getReleaseamount());
			release.put("burntAmount", tm.getBurntamount());
			release.put("type", decodeRewardType(tm.getType()));
			releases.add(release);
		}
		json.put("releases", releases);
		return ResultMap.getSuccessfulResult(json);
	}

	
	@Override
	public ResultMap<?> getPosNodes(Integer type,String address) {
		List<UtgNodeMiner> list = thirdMapper.getPosNodes(type, address);
		JSONArray jsonlist = new JSONArray();
		for(UtgNodeMiner node : list){
			JSONObject item = new JSONObject();
			item.put("node_address", node.getNode_address().toLowerCase());
			item.put("revenue_address", node.getRevenue_address()==null?null:node.getRevenue_address().toLowerCase());
			item.put("manage_address", node.getManage_address()==null?null:node.getManage_address().toLowerCase());
			item.put("node_type", node.getNode_type());
			item.put("active_height", node.getBlocknumber());
			item.put("charge_rate", node.getRate()/100);
			item.put("pledge_amount", node.getTotal_amount());
			item.put("penalty_points", node.getFractions());
			jsonlist.add(item);
		}
		JSONObject json = new JSONObject();
		json.put("list", jsonlist);
		return ResultMap.getSuccessfulResult(json);
	}

	@Override
	public ResultMap<?> getPosNodePledge(String address,Integer status) {
		UtgNodeMiner node = thirdMapper.getPosNodeByAddress(address);
		if (node == null) {
			return ResultMap.getFailureResult("Invaild node address");
		}
		JSONObject json = new JSONObject();
		json.put("node_address", node.getNode_address());
		json.put("revenue_address", node.getRevenue_address());
		json.put("manage_address", node.getManage_address());
		json.put("node_type", node.getNode_type());
		json.put("active_height", node.getBlocknumber());
		json.put("charge_rate", node.getRate()/100);
		json.put("pledge_amount", node.getTotal_amount());
		json.put("penalty_points", node.getFractions());
		List<NodePledge> pledgeList = thirdMapper.getNodePledgeList(address,status);
		JSONArray pledgeJsonlist = new JSONArray();
		for(NodePledge pledge:pledgeList){
			JSONObject pledgeJson = new JSONObject();
			pledgeJson.put("pledge_address", pledge.getPledge_address());
			pledgeJson.put("pledge_hash", pledge.getPledge_hash());
			pledgeJson.put("pledge_amount", pledge.getPledge_amount());
			pledgeJson.put("pledge_number", pledge.getPledge_number());
			pledgeJson.put("pledge_status", pledge.getPledge_status());
			if(pledge.getPledge_status()==0){
				pledgeJson.put("unpledge_number", pledge.getUnpledge_number());
				pledgeJson.put("unpledge_hash", pledge.getUnpledge_hash());
			}
			pledgeJsonlist.add(pledgeJson);
		}
		json.put("pledge_list", pledgeJsonlist);
		return ResultMap.getSuccessfulResult(json);
	}
	
}
