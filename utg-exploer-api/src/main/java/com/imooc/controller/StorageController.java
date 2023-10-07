package com.imooc.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.job.service.AddrTransferService;
import com.imooc.job.service.AddrTransferService.TransferPath;
import com.imooc.pojo.StorageRent;
import com.imooc.pojo.StorageRequest;
import com.imooc.pojo.StorageRevenue;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.PledgeQueryForm;
import com.imooc.pojo.Form.StorageQueryForm;
import com.imooc.pojo.vo.TransferMinerVo;
import com.imooc.service.StorageService;
import com.imooc.service.TransferMinerService;
import com.imooc.utils.Constants;
import com.imooc.utils.ResultMap;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@Api(value = "storage", tags = "storage")
@RequestMapping("/platform")
public class StorageController {
	
	@Autowired
    private StorageService storageService;
	
	@Autowired
	private TransferMinerService transferMinerService;
	
//	@Autowired
//	private AddrTransferService addrTransferService;
	@Value("${remote.server.url:}")
    private String remoteServerUrl;
    @Autowired
    private RestTemplate restTemplate;
		
	@ApiOperation(value = "getStorageSpaceList")
	@PostMapping("/getStorageSpaceList")
	public ResultMap<Page<StorageSpace>> getStorageSpaceList(@Valid @RequestBody StorageQueryForm storagegQueryForm){
		return storageService.getStorageSpaceList(storagegQueryForm);
	}
	
	@ApiOperation(value = "getStorageSpaceInfo")
	@RequestMapping("/getStorageSpaceInfo")
	public ResultMap<StorageSpace> getStorageSpaceInfo(@RequestParam(value="device_addr",required=true) String device_addr,@RequestParam(value="id",required=false) Integer id){
		return storageService.getStorageSpaceInfo(device_addr,id);
	}
	
	@ApiOperation(value = "getStorageRentList")
	@PostMapping("/getStorageRentList")
	public ResultMap<Page<StorageRent>> getStorageRentList(@Valid @RequestBody StorageQueryForm storagegQueryForm){
		return storageService.getStorageRentList(storagegQueryForm);
	}
	
	@ApiOperation(value = "getStorageRentInfo")
	@RequestMapping("/getStorageRentInfo")
	public ResultMap<StorageRent> getStorageRentInfo(@RequestParam(value="rent_hash",required=true) String rent_hash){
		return storageService.getStorageRentInfo(rent_hash);
	}
	
	@ApiOperation(value = "updateStorageSpaceAttach")
	@RequestMapping("/updateStorageSpaceAttach")
	public ResultMap<?> updateStorageSpaceAttach(@RequestParam String device_addr, @RequestParam String text, @RequestParam String pic, @RequestParam String address, @RequestParam String sign) {
		if(remoteServerUrl!=null && !remoteServerUrl.trim().equals("")){
			Map<String,Object> params = new HashMap<>();
			params.put("device_addr", device_addr);
			params.put("text", text);
			params.put("pic", pic);
			params.put("address", address);
			params.put("sign", sign);
			String url = remoteServerUrl+"/platform/updateStorageSpaceAttach?device_addr={device_addr}&text={text}&pic={pic}&address={address}&sign={sign}";						
			String resultString = restTemplate.getForObject(url, String.class, params);
			log.info("Remote get "+url+",params="+params+" ,result="+resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			ResultMap resultMap = new ResultMap(resultJson.getInteger("statusCode"), resultJson.getString("message"), resultJson.get("result"));
			return resultMap;
		}
		return storageService.updateStorageSpaceAttach(device_addr, text, pic, address, sign);
	}
	
	@ApiOperation(value = "updateStorageRentAttach")
	@RequestMapping("/updateStorageRentAttach")
	public ResultMap<?> updateStorageRentAttach(@RequestParam String rent_hash, @RequestParam String text, @RequestParam String pic, @RequestParam String address, @RequestParam String sign) {
		if(remoteServerUrl!=null && !remoteServerUrl.trim().equals("")){
			Map<String,Object> params = new HashMap<>();
			params.put("rent_hash", rent_hash);
			params.put("text", text);
			params.put("pic", pic);
			params.put("address", address);
			params.put("sign", sign);
			String url = remoteServerUrl+"/platform/updateStorageRentAttach?rent_hash={rent_hash}&text={text}&pic={pic}&address={address}&sign={sign}";						
			String resultString = restTemplate.getForObject(url, String.class, params);
			log.info("Remote get "+url+",params="+params+" ,result="+resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			ResultMap resultMap = new ResultMap(resultJson.getInteger("statusCode"), resultJson.getString("message"), resultJson.get("result"));
			return resultMap;
		}
		return storageService.updateStorageRentAttach(rent_hash, text, pic, address, sign);
	}
	
	@ApiOperation(value = "getStorageRentAttachFile")
	@RequestMapping("/getStorageRentAttachFile")
	public void getStorageRentAttachFile(HttpServletRequest request,HttpServletResponse response, @RequestParam String rent_hash){
		storageService.getStorageRentAttachFile(request, response, rent_hash);
	}
	
	@ApiOperation(value = "getStoragePledgeList")
	@PostMapping("/getStoragePledgeList")
	public ResultMap<Page<StorageRequest>> getStoragePledgeList(@Valid @RequestBody StorageQueryForm storagegQueryForm){
		return storageService.getStorageRequestList(storagegQueryForm);
	}
	
	@ApiOperation(value = "getStorageRequestList")
	@PostMapping("/getStorageRequestList")
	public ResultMap<Page<StorageRequest>> getStorageRequestList(@Valid @RequestBody StorageQueryForm storagegQueryForm){
		return storageService.getStorageRequestList(storagegQueryForm);
	}

	@ApiOperation(value = "getStorageRequestInfo")
	@RequestMapping("/getStorageRequestInfo")
	public ResultMap<StorageRequest> getStorageRequestInfo(@RequestParam(value="reqid",required=false) String reqid,
			@RequestParam(value="device_addr",required=false) String device_addr,
			@RequestParam(value="rent_hash",required=false) String rent_hash){
		return storageService.getStorageRequestById(reqid,device_addr,rent_hash);
	}	
	
	@ApiOperation(value = "getStoragePledgeInfo")
	@RequestMapping("/getStoragePledgeInfo")
	public ResultMap<?> getStoragePledgeInfo(@RequestParam(value="device_addr",required=true) String device_addr){
		return storageService.getStoragePledgeInfo(device_addr);
	}
	
	@ApiOperation(value = "getStorageBasicSet")
    @RequestMapping("/getStorageBasicSet")
	public ResultMap<?> getStorageBasicSet(){
		return storageService.getStorageBasicSet();
	}
	
	@ApiOperation(value = "getStorageConfigList")
    @RequestMapping("/getStorageConfigList")
	public ResultMap<?> getStorageConfigList(@RequestParam(value="type",required=true) String type){
		return storageService.getStorageConfigList(type);
	}
	
	
	@ApiOperation(value = "getStorageRevenueList")
	@PostMapping("/getStorageRevenueList")
	public ResultMap<Page<StorageRevenue>> getStorageRevenueList(@Valid @RequestBody StorageQueryForm storagegQueryForm){
		return storageService.getStorageRevenueList(storagegQueryForm);
	}
	
	@ApiOperation(value = "getStorageRevenueInfo")
	@RequestMapping("/getStorageRevenueInfo")
	public ResultMap<StorageRevenue> getStorageRevenueInfo(@RequestParam(value="revenue_addr",required=true) String revenue_addr,
			@RequestParam(value="type",required=false) Integer type,@RequestParam(value="types",required=false) Integer[] types){
		return storageService.getStorageRevenueInfo(revenue_addr,type,types);
	}
		
	
	@ApiOperation(value = "getPledgeNodeList")	
    @PostMapping("/getPledgeNodeList")
    public ResultMap<Page<?>> getPledgeNodeList(@Valid @RequestBody PledgeQueryForm pledgeQueryForm) throws Exception {
		pledgeQueryForm.setAddress(Constants.pre0XtoNX(pledgeQueryForm.getAddress()));        
        return storageService.getPledgeNodeList(pledgeQueryForm);
    }
		
	@ApiOperation(value = "getPledgeNodeStat")	
    @PostMapping("/getPledgeNodeStat")
    public ResultMap<?> getPledgeNodeStat(@Valid @RequestBody PledgeQueryForm pledgeQueryForm) throws Exception {
		pledgeQueryForm.setAddress(Constants.pre0XtoNX(pledgeQueryForm.getAddress()));        
        return storageService.getPledgeNodeStat(pledgeQueryForm);
    }
	

	@ApiOperation(value = "getStorageRewardList")	
    @PostMapping("/getStorageRewardList")
    public ResultMap<Page<TransferMinerVo>> getRewardList(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        blockQueryForm.setRevenueAddress(Constants.pre0XtoNX(blockQueryForm.getRevenueAddress()));
        blockQueryForm.setAddr(Constants.pre0XtoNX(blockQueryForm.getAddr()));
        
        return transferMinerService.getRewardList(blockQueryForm);
    }	
	
	@ApiOperation(value = "getStorageRewardStat")	
    @PostMapping("/getStorageRewardStat")
    public ResultMap<?> getRewardStat(@Valid @RequestBody BlockQueryForm blockQueryForm) throws Exception {
        blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
        blockQueryForm.setRevenueAddress(Constants.pre0XtoNX(blockQueryForm.getRevenueAddress()));  
        blockQueryForm.setAddr(Constants.pre0XtoNX(blockQueryForm.getAddr()));
        return transferMinerService.getRewardStat(blockQueryForm);
    }
	
	@ApiOperation(value = "getStorageRewardInfo")	
	@RequestMapping("/getStorageRewardInfo")
    public ResultMap<TransferMinerVo> getRewardInfo(@RequestParam(value="id",required=true) String id) throws Exception {       
        return transferMinerService.getRewardInfo(id);
    }
		
	@ApiOperation(value = "getTransactionRentList")	
	@RequestMapping("/getTransactionRentList")
	public ResultMap<Page<?>> getTransactionRentList(@Valid @RequestBody StorageQueryForm storageQueryForm){
		storageQueryForm.setTxhash(Constants.pre0XtoNX(storageQueryForm.getTxhash()));
		return storageService.getTransactionRentList(storageQueryForm);
	}
	
	@ApiOperation(value = "predictStoragePledgeAmount")	
    @PostMapping("/predictStoragePledgeAmount")
	public ResultMap<Map<?,?>> predictStoragePledgeAmount(@Valid @RequestBody JSONObject json){
		if(json==null || !json.containsKey("declare_space") || !json.containsKey("bandwidth"))
			throw new RuntimeException("Invaild requestboy "+json);
		BigDecimal declare_space = json.getBigDecimal("declare_space");
		Integer bandwidth = json.getInteger("bandwidth");
		return storageService.predictStoragePledgeAmount(declare_space, bandwidth);
	}
	
	@ApiOperation(value = "calStorageLeaseReward")	
    @PostMapping("/calStorageLeaseReward")
	public ResultMap<Map<?, ?>> calStorageLeaseReward(@Valid @RequestBody JSONObject json) {
		BigDecimal rentCapacity = json.getBigDecimal("rentCapacity");
		BigDecimal rentPrice = json.getBigDecimal("rentPrice");
		BigDecimal rentDays = json.getBigDecimal("rentDays");
		BigDecimal bandwidthIndex = json.getBigDecimal("bandwidthIndex");
		BigDecimal storageIndex = json.getBigDecimal("storageIndex");
		return storageService.calStorageLeaseReward(rentCapacity, rentPrice, rentDays, bandwidthIndex, storageIndex);
	}
	
	
	@PostMapping("/getTransferPath")
	public ResultMap<?> getTransferPath(@Valid @RequestBody JSONObject json){
		String from = Constants.pre0XtoNX(json.getString("from"));
		String to = Constants.pre0XtoNX(json.getString("to"));
		Long startblock = json.getLong("startblock");
		Long endblock = json.getLong("endblock");
		Integer maxdepth = json.getInteger("maxdepth");		
		List<TransferPath> result = AddrTransferService.getInstance().getTransferPath(from, to, startblock, endblock, maxdepth);
		return ResultMap.getSuccessfulResult(result);
	}
	@ApiOperation(value = "getSnListBySpHash")
	@PostMapping("/getSnListBySpHash")
	public ResultMap<Page<?>> getSnListBySpHash(@Valid @RequestBody StorageQueryForm storagegQueryForm){
		if (storagegQueryForm.getSpHash()==null ||storagegQueryForm.getSpHash().equals("")) {
			return ResultMap.getApiFailureResult("spHash is null");
		}
		return storageService.getSNListBySpHash(storagegQueryForm);
	}
}
