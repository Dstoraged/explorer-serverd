package com.imooc.job.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.imooc.Utils.TimeSpend;
import com.imooc.mapper.StatisticMapper;
import com.imooc.pojo.StatGlobal;

import lombok.extern.slf4j.Slf4j;
import wiremock.org.apache.http.HttpResponse;
import wiremock.org.apache.http.client.methods.HttpPost;
import wiremock.org.apache.http.entity.StringEntity;
import wiremock.org.apache.http.impl.client.CloseableHttpClient;
import wiremock.org.apache.http.impl.client.HttpClients;
import wiremock.org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
public class StatisticService {

	@Autowired
	StatisticMapper statisticMapper;

	@Value("${epoch}")
	private long epoch;
	@Value("${dayOneNumber}")
	private Long dayOneNumber;
	@Value("${ipaddress}")
	private String ipaddress;
	public void saveGlobalStat(Long blockNumber) {
		StatGlobal statGlobal = statisticMapper.statGlobalData();
		if (statGlobal == null)
			statGlobal = new StatGlobal();
		Long next_election;
		if (blockNumber <= epoch) {
			next_election = epoch;
		} else {
			next_election = (blockNumber + (epoch - blockNumber % epoch));
		}

		statGlobal.setBlocknumber(blockNumber);
	//	statGlobal.setBlockdate(blockDate);
		statGlobal.setNext_election(next_election.toString());
		statisticMapper.saveGlobalStat(statGlobal);
	}
	
	@Async
	public void updateRewardStat(Long blocknumber) {
		TimeSpend timeSpend = new TimeSpend();
		long dayBlocknumber = (blocknumber / dayOneNumber) * dayOneNumber;
		statisticMapper.updateGlobalReward(dayBlocknumber);		
		log.info("updateRewardStat at " + blocknumber +" and dayBlocknumber="+dayBlocknumber +" spend "+timeSpend.getSpendTime());
		TimeSpend timeSpend1 = new TimeSpend();
		BigDecimal totalSpace=getTotalLeaseSpace(blocknumber);
		statisticMapper.updateGlobalRelease(totalSpace);
		log.info("updateGlobalRelease leae_his at " + blocknumber +" and totalSpace="+totalSpace +" spend "+timeSpend1.getSpendTime());

	}
	private  BigDecimal getTotalLeaseSpace(Long blockNumber){
		JSONObject param = new JSONObject(true);
		param.put("jsonrpc", "2.0");
		param.put("method", "alien_getSnapshotTLSAtNumber");
		param.put("params", new long[]{blockNumber});
		param.put("id", 1);
		JSONObject result = new JSONObject();
		String url = ipaddress;
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient client = HttpClients.createDefault();
		//req parameter transfer json
		StringEntity entity = new StringEntity(param.toString(), "UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		try {
			HttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = JSONObject.parseObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
				JSONObject json=result.getJSONObject("result");
				if(json!=null){
					return json.getBigDecimal("totalleasespace");
				}
			}
		} catch (IOException e) {
			log.error("get totalleasespace error",e);
			result.put("error","get totalleasespace error");
		}finally {
			try{
				if(client!=null){
					client.close();
				}
			}catch (Exception e){ }
		}
		return new BigDecimal("1171346970183497");
	}
}
