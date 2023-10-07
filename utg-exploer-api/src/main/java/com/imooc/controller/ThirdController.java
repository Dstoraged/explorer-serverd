package com.imooc.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.RateLimiter;
import com.imooc.mapper.ThirdMapper;
import com.imooc.pojo.ThirdapiLog;
import com.imooc.service.ThirdSevice;
import com.imooc.utils.Constants;
import com.imooc.utils.HttpUtils;
import com.imooc.utils.ResultMap;
import com.imooc.utils.SizedStack;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@RestController
//@RequestMapping("/third")
public class ThirdController {

	@Value("${thirdApi.enableApi:true}")
	private boolean enableApi;
	@Value("${thirdApi.allowPerMin:6}")
	private int allowPerMin;
//	@Value("${thirdApi.allowPerSecond:0.1}")
//	private double allowPerSecond;	
	@Value("${thirdApi.useRateLimiter:true}")
	private boolean useRateLimiter;
	@Value("${thirdApi.enableSaveLog:false}")
	private boolean enableSaveLog;
	@Value("${thirdApi.enableBlackList:false}")
	private boolean enableBlackList;

	@Autowired
	private ThirdMapper thirdMapper;
	@Autowired
	private ThirdSevice thirdSevice;

	@SuppressWarnings("rawtypes")
	private TimedCache<String, ResultMap> cache = CacheUtil.newTimedCache(3600 * 1000L);

	private RateLimiter rateLimiter;	// = RateLimiter.create(0.1, 1, TimeUnit.SECONDS);
	
	private static Map<String, SizedStack<Long>> addrRestrict = new HashMap<>();

//	private static Map<String, Boolean> addrWorking = new HashMap<>();
	private boolean working = false;

	@PostConstruct
	public void init(){
		double allowPerSecond = (double) allowPerMin / 60;
		rateLimiter = RateLimiter.create(allowPerSecond, 1, TimeUnit.SECONDS);
	}
	
	@RequestMapping("/getRevenue")
	public ResultMap<?> getRevenue(@RequestParam(value = "address") String address, HttpServletRequest request) {
		String validResult = validateRequest(request);
		if (validResult != null)
			return ResultMap.getApiFailureResult(validResult);
		try {
			address = Constants.prefixAddress(address);
			String cachekey = "getRevenue#" + address;
			ResultMap<?> result = cache.get(cachekey, false);
			if (result == null) {
				result = thirdSevice.getRevenue(address);
				cache.put(cachekey, result);
			}
			return result;
		} finally {
			finishRequest(request);
		}

	}

	@RequestMapping("/getRewards")
	public ResultMap<?> getRewards(@RequestParam(value = "address") String address, @RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "startblock", required = false) Long startblock, @RequestParam(value = "endblock", required = false) Long endblock,
			@RequestParam(value = "datetime", required = false) Long datetime, HttpServletRequest request) {
		String validResult = validateRequest(request);
		if (validResult != null)
			return ResultMap.getApiFailureResult(validResult);
		try {
			address = Constants.prefixAddress(address);
			String cachekey = "getRewards#" + address + "-" + type + "-" + startblock + "-" + endblock + "-" + datetime;
			ResultMap<?> result = cache.get(cachekey, false);
			if (result == null) {
				result = thirdSevice.getRewards(address, type, startblock, endblock, datetime);
				cache.put(cachekey, result);
			}
			return result;
		} finally {
			finishRequest(request);
		}
	}

	@RequestMapping("/getTotalRewards")
	public ResultMap<?> getTotalRewards(@RequestParam(value = "address") String address, @RequestParam(value = "type", required = false) Integer type,
			HttpServletRequest request) {
		String validResult = validateRequest(request);
		if (validResult != null)
			return ResultMap.getApiFailureResult(validResult);
		try {
			address = Constants.prefixAddress(address);
			String cachekey = "getTotalRewards#" + address + "-" + type;
			ResultMap<?> result = cache.get(cachekey, false);
			if (result == null) {
				result = thirdSevice.getTotalRewards(address, type);
				cache.put(cachekey, result);
			}
			return result;
		} finally {
			finishRequest(request);
		}
	}

	@RequestMapping("/getReleases")
	public ResultMap<?> getReleases(@RequestParam(value = "address") String address, @RequestParam(value = "type", required = false) Integer type,
			HttpServletRequest request) {
		String validResult = validateRequest(request);
		if (validResult != null)
			return ResultMap.getApiFailureResult(validResult);
		try {
			address = Constants.prefixAddress(address);
			String cachekey = "getReleases#" + address + "-" + type;
			ResultMap<?> result = cache.get(cachekey, false);
			if (result == null) {
				result = thirdSevice.getReleases(address, type);
				cache.put(cachekey, result);
			}
			return result;
		} finally {
			finishRequest(request);
		}
	}

	@RequestMapping("/getPosNodes")
	public ResultMap<?> getPosNodes(@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "address", required = false) String address, HttpServletRequest request) {
		String validResult = validateRequest(request);
		if (validResult != null)
			return ResultMap.getApiFailureResult(validResult);
		try {
			address = Constants.prefixAddress(address);
			String cachekey = "getPosNodes#" + type + "-" + address;
			ResultMap<?> result = cache.get(cachekey, false);
			if (result == null) {
				result = thirdSevice.getPosNodes(type, address);
				cache.put(cachekey, result);
			}
			return result;
		} finally {
			finishRequest(request);
		}
	}

	@RequestMapping("/getPosNodePledge")
	public ResultMap<?> getPosNodePledge(@RequestParam(value = "address") String address,
			@RequestParam(value = "status", required = false) Integer status, HttpServletRequest request) {
		String validResult = validateRequest(request);
		if (validResult != null)
			return ResultMap.getApiFailureResult(validResult);
		try {
			address = Constants.prefixAddress(address);
			String cachekey = "getPosNodePledge#" + address + "-" + status;
			ResultMap<?> result = cache.get(cachekey, false);
			if (result == null) {
				result = thirdSevice.getPosNodePledge(address, status);
				cache.put(cachekey, result);
			}
			return result;
		} finally {
			finishRequest(request);
		}
	}

	
	
	private String validateRequest(HttpServletRequest request) {
		String remoteAddr = HttpUtils.getRealIp(request);		
		ThirdapiLog thirdapiLog = new ThirdapiLog();
		thirdapiLog.setIpaddr(remoteAddr);
		thirdapiLog.setTimestamp(new Date());
		thirdapiLog.setRequest_path(request.getServletPath());
		thirdapiLog.setRequest_url(request.getRequestURL().toString());
		thirdapiLog.setQuery_string(request.getQueryString());
		
		if(!enableApi){
			//Disabled
			thirdapiLog.setLimited(4);
			if (enableSaveLog)
				thirdMapper.saveLog(thirdapiLog);
			log.info("Thirdapi disabled and request for " + thirdapiLog);
			return "The interface is under maintenance, please try again later.";
		}
		if(useRateLimiter){
			//RateLimiter
			if (!rateLimiter.tryAcquire()) {
				thirdapiLog.setLimited(1);
				if (enableSaveLog)
					thirdMapper.saveLog(thirdapiLog);
				log.info("Thirdapi frequency restrict for " + thirdapiLog);
				return "Api request is too frequent, Please try again later.";
			}
		}else{	
			//Working
		//	Boolean working = addrWorking.get(remoteAddr);
			if (working) {
				thirdapiLog.setLimited(2);
				if (enableSaveLog)
					thirdMapper.saveLog(thirdapiLog);
				log.info("Thirdapi working for " + thirdapiLog);
				return "Other request is in progress, please try again later.";
			}
			//Blacklisted
			if (enableBlackList) {
				List<String> blacklist = thirdMapper.getBlacklist();
				if (blacklist.contains(remoteAddr)) {
					thirdapiLog.setLimited(3);
					if (enableSaveLog)
						thirdMapper.saveLog(thirdapiLog);
					log.info("Thirdapi blacklist restrict for " + thirdapiLog);
					return "Your ip address has been blacklisted, please contact us.";
				}
			}
			//Frequent
			SizedStack<Long> timeStack = addrRestrict.get(remoteAddr);
			if (timeStack == null) {
				timeStack = new SizedStack<Long>(Long.class, allowPerMin);
				addrRestrict.put(remoteAddr, timeStack);
			}
			long timestamp = System.currentTimeMillis();
			Long firsttime = timeStack.first();
			Long interval = null;
			if (firsttime != null)
				interval = (timestamp - firsttime) / 1000;
			if (firsttime != null && timeStack.length() >= allowPerMin && timestamp - firsttime < 1000L * 60) {
				thirdapiLog.setInterval(interval);
				thirdapiLog.setLimited(1);
				if (enableSaveLog)
					thirdMapper.saveLog(thirdapiLog);
				log.info("Thirdapi frequency restrict for " + thirdapiLog);
				return "Your request is too frequent, Please try again later.";
			}
			//Normal
			timeStack.push(timestamp);
		}
		thirdapiLog.setLimited(0);
		if (enableSaveLog)
			thirdMapper.saveLog(thirdapiLog);
		log.info("Thirdapi request for " + thirdapiLog);

	//	addrWorking.put(remoteAddr, true);
		working = true;
		return null;
	}

	private void finishRequest(HttpServletRequest request) {
	//	String remoteAddr = HttpUtils.getRealIp(request);
	//	addrWorking.put(remoteAddr, false);
		working = false;
	}
}
