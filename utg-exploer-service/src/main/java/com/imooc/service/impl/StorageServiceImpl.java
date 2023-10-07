package com.imooc.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imooc.mapper.*;
import com.imooc.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.util.StringUtil;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.PledgeQueryForm;
import com.imooc.pojo.Form.StorageQueryForm;
import com.imooc.pojo.vo.TransferMinerVo;
import com.imooc.service.StorageService;
import com.imooc.utils.Constants;
import com.imooc.utils.FileUtil;
import com.imooc.utils.NumericUtil;
import com.imooc.utils.ResultMap;
import com.imooc.utils.Web3jUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService{
	
	protected static Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
	
	@Autowired
	private StorageConfigMapper storageConfigMapper;
	
	@Autowired
	private StorageSpaceMapper storageSpaceMapper;
	
	@Autowired
	private StorageRentMapper storageRentMapper;
	
	@Autowired
	private StorageRequestMapper storageRequestMapper;
	
	@Autowired
	private StorageRevenueMapper storageRevenueMapper;
	
	@Autowired
	private TransactionMapper transactionMapper;
	
	@Autowired
	private TransferMinerMapper transferMinerMapper;
	
	@Autowired
	private GlobalConfigMapper globalConfigMapper;
	@Autowired
	private StatisticMapper statisticMapper;
	@Autowired
	private StorageService storageService;
	@Value("${ipaddress}")
    private String ipaddress;	
	@Value("${dayOneNumber}")
	private Long dayOneNumber;

	@Value("${lockreleaseinterval:360}")
    private Long lockreleaseinterval;
	@Value("${rewardcollectdelay:0}")
	private Integer rewardcollectdelay;
	
	
	@Override
	public ResultMap<?> getStorageBasicSet() {
		Map<String,String> setting = new HashMap<String,String>();
		/*List<Transaction> txList = transactionMapper.getTransactionByTxType("stset");		
		for(Transaction tx : txList){
			String key = tx.getParam1();
			String value = tx.getParam2();
			if(!setting.containsKey(key)){
				setting.put(key, value);
			}
		}
		Transaction exchRate = transactionMapper.getLatestTransactionByTxType("ExchRate");
		if(exchRate!=null)
			setting.put("ExchRate", exchRate.getParam1());*/
		List<GlobalConfig> list = globalConfigMapper.getList();
		for(GlobalConfig config : list){
			setting.put(config.getType(), config.getValue());
		}		
		return ResultMap.getSuccessfulResult(setting);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<List<StorageConfig>> getStorageConfigList(String type){
		List<StorageConfig> list = storageConfigMapper.getList(type);
		return ResultMap.getSuccessfulResult(list);		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<StorageSpace>> getStorageSpaceList(StorageQueryForm storageQueryForm) {		
		Page page = storageQueryForm.newFormPage();
		Long pageSize = page.getSize();
		Long pageCurrent = page.getCurrent();
		pageCurrent = (pageCurrent - 1) * pageSize;
		storageQueryForm.setCurrent(pageCurrent);
		
		List<StorageSpace> list = storageSpaceMapper.getPageList(storageQueryForm);	
	//	long currentNumber = blockMapper.getLeatestBlockNumber();			
	//	for(StorageSpace space : list){
	//		setSpaceVaildStatus(space,currentNumber);				
	//	}		
		long total = storageSpaceMapper.getTotal(storageQueryForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}
	//TODO
	protected void setSpaceVaildStatus(StorageSpace space,long currentNumber){
	//	long currentNumber2 = currentNumber - lockreleaseinterval - rewardcollectdelay;
		long currentDays = currentNumber / dayOneNumber;
		Long vaildNumber = space.getValidNumber();
		Long succNumber = space.getSuccNumber();
		Long pledgeNumber = space.getPledgeNumber();
		if(space.getVaild24Status()==null){
			if (vaildNumber != null && vaildNumber != 0 && succNumber != null && succNumber != 0) {				
				long vaildDays = vaildNumber / dayOneNumber;			
				if (currentDays - vaildDays <= 1 && pledgeNumber != null && !pledgeNumber.equals(vaildNumber)) {
					Integer vaild24Status = vaildNumber.equals(succNumber) ? 1 : 0;
		//			log.warn(space.getDeviceAddr()+" Vaild24Status Inconsistent :"+vaild24Status+"!="+space.getVaild24Status());
					space.setVaild24Status(vaild24Status);
				}
		//		log.warn("address:"+space.getDeviceAddr()+",lockreleaseinterval="+lockreleaseinterval+",currentNumber:"+currentNumber+",currentDays="+currentDays+
		//				",vaildNumber="+vaildNumber+",vaildDays:"+vaildDays+",vaild24Status:"+space.getVaild24Status());
			}
		}
		if(space.getVaildProgress()==null){
			Integer vaildStatus = space.getVaildStatus();
			currentDays = currentNumber / dayOneNumber;
			long pledgeDays = pledgeNumber / dayOneNumber;			
			BigDecimal vaildProgress = (vaildStatus!=null && vaildStatus==0) || pledgeDays == currentDays
					? new BigDecimal(100) : BigDecimal.ZERO;
		//	log.warn(space.getDeviceAddr()+" VaildProgree Inconsistent :"+vaildProgress+"!="+space.getVaildProgress());
		//	log.warn("currentNumber:"+currentNumber+",currentDays="+currentDays+ ",pledgeNumber="+pledgeNumber+",pledgeDays="+pledgeDays+", pledgeNumber="+ pledgeNumber+",vaildStatus="+vaildStatus);
			space.setVaildProgress(vaildProgress);  
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<StorageSpace> getStorageSpaceInfo(String device_addr,Integer id) {
		device_addr = Constants.prefixAddress(device_addr);
		StorageSpace space;
		if(id==null)
			space = storageSpaceMapper.getSpaceInfo(device_addr);
		else
			space = storageSpaceMapper.getSpaceById(device_addr,id);
		if(space!=null){
		//	long currentNumber = blockMapper.getLeatestBlockNumber();
		//	setSpaceVaildStatus(space,currentNumber);			
			return ResultMap.getSuccessfulResult(space);
		}else{
			return ResultMap.getFailureResult("query no data");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<StorageRent>> getStorageRentList(StorageQueryForm storageQueryForm) {		
		Page page = storageQueryForm.newFormPage();
		Long pageSize = page.getSize();
		Long pageCurrent = page.getCurrent();
		pageCurrent = (pageCurrent - 1) * pageSize;
		storageQueryForm.setCurrent(pageCurrent);
		List<StorageRent> list = storageRentMapper.getPageList(storageQueryForm);
		long total = storageRentMapper.getTotal(storageQueryForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}


	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<StorageRent> getStorageRentInfo(String rent_hash) {
		StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
		if(rent!=null){
			return ResultMap.getSuccessfulResult(rent);
		}else{
			return ResultMap.getFailureResult("query no data");
		}
	}
	/*
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<StorageRequest>> getStoragePledgeList(StorageQueryForm storageQueryForm) {
		Page page = storageQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        storageQueryForm.setCurrent(pageCurrent);
        storageQueryForm.setStatus(0);
        List<StorageRequest> list = storageRequestMapper.getPageList(storageQueryForm);
		long total = storageRequestMapper.getTotal(storageQueryForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<StorageRequest>> getStorageRequestList(StorageQueryForm storageQueryForm) {		
		Page page = storageQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        storageQueryForm.setCurrent(pageCurrent);
        List<StorageRequest> list = storageRequestMapper.getPageList(storageQueryForm);
		long total = storageRequestMapper.getTotal(storageQueryForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<StorageRequest> getStorageRequestById(String reqid,String device_addr,String rent_hash) {
		if(StringUtil.isEmpty(reqid) && StringUtil.isEmpty(device_addr) && StringUtil.isEmpty(rent_hash))
			return ResultMap.getFailureResult("Missing required parameters");	
		StorageRequest request = storageRequestMapper.getRequestById(reqid,device_addr,rent_hash);
		if(request!=null){
			return ResultMap.getSuccessfulResult(request);
		}		
		return ResultMap.getFailureResult("query no data");		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<StorageRevenue>> getStorageRevenueList(StorageQueryForm storageQueryForm) {
		Page page = storageQueryForm.newFormPage();
		Long pageSize = page.getSize();
		Long pageCurrent = page.getCurrent();
		pageCurrent = (pageCurrent - 1) * pageSize;
		storageQueryForm.setCurrent(pageCurrent);
		List<StorageRevenue> list = storageRevenueMapper.getPageList(storageQueryForm);
		long total = storageRevenueMapper.getTotal(storageQueryForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}


	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<StorageRevenue> getStorageRevenueInfo(String revenue_addr,Integer type,Integer[] types) {
		StorageRevenue revenue ;
		if(type==null && types==null)
			revenue = storageRevenueMapper.getRevenueInfo(revenue_addr);
		else
			revenue = storageRevenueMapper.getRevenueInfoTyped(revenue_addr,type,types);
		if(revenue!=null){
			return ResultMap.getSuccessfulResult(revenue);
		}
		revenue = storageRevenueMapper.getRevenueStat(revenue_addr,type,types);
		if(revenue!=null){
			return ResultMap.getSuccessfulResult(revenue);
		}
		return ResultMap.getFailureResult("query no data");		
	}
	
	
	


	@Override
	public ResultMap<?> getStoragePledgeInfo(String device_addr) {
		device_addr = Constants.prefixAddress(device_addr);
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if(space!=null){
			List<StorageRent> rentList = storageRentMapper.getList(device_addr, null, "1");
			JSONObject data = new JSONObject();
			data.put("statue", space.getPledgeStatus());
			JSONArray list = new JSONArray();
			for(StorageRent rent : rentList){
				list.add(rent.getRentHash());
			//	JSONObject item = new JSONObject();
			//	item.put(rent.getRentHash(), rent.getRentStatus());
			}
			data.put("rent_hashs", list);
			return ResultMap.getSuccessfulResult(data);
		}else{
			return ResultMap.getFailureResult("query no data");
		}		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<?>> getPledgeNodeList(PledgeQueryForm pledgeQueryForm) {
		Page page = pledgeQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        pledgeQueryForm.setCurrent(pageCurrent);
        List<?> list = storageSpaceMapper.getPledgeNodeList(pledgeQueryForm);
		long total = storageSpaceMapper.getPledgeNodeTotal(pledgeQueryForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<Map<String,Object>> getPledgeNodeStat(PledgeQueryForm pledgeQueryForm){
		Map<String, Object> data = storageSpaceMapper.getPledgeNodeStat(pledgeQueryForm);
		return ResultMap.getSuccessfulResult(data);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ResultMap<Page<?>> getTransactionRentList(StorageQueryForm storageQueryForm) {
		String txhash = storageQueryForm.getTxhash();
		if(StringUtil.isEmpty(txhash))
			return ResultMap.getFailureResult("Missing required parameters");
		Transaction tx = transactionMapper.getTransactionByTxHash(txhash);
		if(tx==null)
			return ResultMap.getFailureResult("query no data");
		Page page = storageQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        storageQueryForm.setCurrent(pageCurrent);
        List<StorageRent> list = new ArrayList<>();
        long total = 0;
		String param5 = tx.getParam5();
		String param6 = tx.getParam6();
		if(!StringUtil.isEmpty(param6)){
			String[] rent_hashs = param6.split(",");
			List<String> succ_hashs = StringUtil.isEmpty(param5) ? new ArrayList<String>(): Arrays.asList(param5.split(","));
			storageQueryForm.setRent_hashs(rent_hashs);			
			list = storageRentMapper.getPageList(storageQueryForm);
			for(StorageRent item:list){
				int status = succ_hashs.contains(item.getRentHash().toLowerCase()) ? 1 : 0;
				item.setVaildTxStatus(status);
			}
			total = storageRentMapper.getTotal(storageQueryForm);
		}
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
		
	}
	
	
	    

	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<Map<?,?>> predictStoragePledgeAmount(BigDecimal declare_space, Integer bandwidth) {
		Long blockNumber;
		try {
			Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
			blockNumber = web3j.ethBlockNumber().send().getBlockNumber().longValue();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		double plbwRatio = NumericUtil.getBandwidthPledgeRatio(bandwidth);		
		double factor = 0.4;		
		long storageEffectBlockNumber = 834261;
		long blockNumPerYear = dayOneNumber * 365;
		BigDecimal defaultTbAmount = new BigDecimal("1250000000000000000"); // 1.25 UTG
		BigDecimal tbPledgeNum = defaultTbAmount;
		if (blockNumber > storageEffectBlockNumber + blockNumPerYear) { 

			BlockQueryForm blockQueryForm = new BlockQueryForm();
			blockQueryForm.setTypes(new Integer[] { 1, 9, 5 });
			TransferMinerVo tm = transferMinerMapper.getMinerSum(blockQueryForm);
			BigDecimal totalReward = tm.getTotalAmount();

			StorageQueryForm storageQueryForm = new StorageQueryForm();
			storageQueryForm.setType(0);
			StorageSpace stat = storageSpaceMapper.getStat(storageQueryForm);
			BigDecimal total_space = stat.getDeclareSpace();

			BigDecimal totalSpace = total_space.divide(new BigDecimal("1099511627776")); // B-> TB
			if (totalSpace.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal calTbPledgeNum = totalReward.divide(totalSpace,BigDecimal.ROUND_CEILING);
				if (calTbPledgeNum.compareTo(defaultTbAmount) < 0) {
					tbPledgeNum = calTbPledgeNum;
				}
			}
		}
		// return (declareCapacity.Div(decimal.NewFromBigInt(tb1b,0))).Mul(tbPledgeNum).Mul(plbwRatio).Mul(factor).BigInt()
		BigDecimal amount = declare_space.divide(new BigDecimal("1099511627776"),BigDecimal.ROUND_CEILING)
				.multiply(tbPledgeNum).multiply(new BigDecimal(plbwRatio)).multiply(new BigDecimal(factor));
	
		Map<String,Object> result = new HashMap<>();
		result.put("utg",amount.toBigInteger().toString());
        return ResultMap.getSuccessfulResult(result);
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<Map<?, ?>> calStorageLeaseReward(BigDecimal rentCapacity, BigDecimal rentPrice, BigDecimal rentDays, BigDecimal bandwidthIndex, BigDecimal storageIndex) {

		String typeVal=globalConfigMapper.getTypeValue("9");
		BigDecimal basePrice=new BigDecimal(typeVal==null?"80000000000000":typeVal);
		basePrice=basePrice.divide(BigDecimal.valueOf(10).pow(18));
	//	String value = globalConfigMapper.getTypeValue("4");
	//	if(value!=null)
	//		basePrice = new BigDecimal(value).divide(BigDecimal.valueOf(10).pow(18));
		StorageQueryForm storageQueryForm = new StorageQueryForm();
		storageQueryForm.setType(0);
		StatGlobal stat = statisticMapper.getGlobalStat();
		BigDecimal gbUtgRate = getGbUtgRate(stat.getLease_his());
		if(gbUtgRate!=null){
			gbUtgRate=BigDecimal.valueOf(10).pow(18).divide(gbUtgRate , 4, BigDecimal.ROUND_HALF_DOWN);
		}
		BigDecimal leaseReward = getStorageLeaseReward(rentCapacity, rentPrice, rentDays, bandwidthIndex, storageIndex, basePrice, gbUtgRate);
       if(leaseReward!=null){
		   leaseReward=leaseReward.setScale(18, RoundingMode.HALF_UP) ;
	   }
		Map<String, Object> result = new HashMap<>();
	  	result.put("gbUtgRate",gbUtgRate );
		result.put("leaseReward", leaseReward);
		return ResultMap.getSuccessfulResult(result);
	}
	@Override
	public  BigDecimal getGbUtgRate(BigDecimal totalLeaseSpace) {
		if(totalLeaseSpace==null) {
			return BigDecimal.ZERO;
		}
		BigDecimal tb1b = new BigDecimal("1099511627776");
		BigDecimal totalBlockReward = new BigDecimal(52500000).multiply(BigDecimal.valueOf(10).pow(18));
		BigDecimal oneEb = tb1b.multiply(BigDecimal.valueOf(1048576));
		log.info("totalLeaseSpace :"+totalLeaseSpace);
		BigDecimal[] results =totalLeaseSpace==null?new BigDecimal[0]: totalLeaseSpace.divideAndRemainder(oneEb);
		BigDecimal modeeb =results.length>1? results[1]:BigDecimal.ZERO;
		BigDecimal neb = BigDecimal.valueOf(1);
		if (totalLeaseSpace.compareTo(oneEb) == 1) {
			neb = totalLeaseSpace.divide(oneEb);
			if (modeeb.compareTo(BigDecimal.ZERO) == 1) {
				neb = neb.add(BigDecimal.valueOf(1));
			}
		}
		BigDecimal pwern = new BigDecimal("0.9986146661010289");
		//Total_UTG(PoTS)×(1-0.5^n/500)  1EB rewards
		BigDecimal ebReward = totalBlockReward.multiply(BigDecimal.valueOf(1).subtract(pwern.pow(neb.intValue())));
		BigDecimal beforebReward = BigDecimal.ZERO;
		if (neb.compareTo(BigDecimal.valueOf(1)) == 1) {
			BigDecimal beforeNeb = neb.subtract(BigDecimal.valueOf(1));
			beforebReward = totalBlockReward.multiply(BigDecimal.valueOf(1).subtract(pwern.pow(beforeNeb.intValue())));
		}
		ebReward = ebReward.subtract(beforebReward);
		BigDecimal gbUTGRate = ebReward.divide(new BigDecimal("1073741824"),BigDecimal.ROUND_CEILING);
		return gbUTGRate;
	}

	private BigDecimal getStorageLeaseReward(BigDecimal rentCapacity, BigDecimal rentPrice, BigDecimal rentDays,
			BigDecimal bandwidthIndex, BigDecimal storageIndex, BigDecimal basePrice, BigDecimal gbUTGRate) {
		BigDecimal priceIndex = BigDecimal.valueOf(1);
		BigDecimal priceRate = rentPrice.divide(basePrice);
		if (rentPrice.compareTo(basePrice) == 1) {
			priceIndex = BigDecimal.valueOf(1.04);
		} else if (rentPrice.compareTo(basePrice) == -1) {
			priceIndex = new BigDecimal(1/1.04);
		}
		BigDecimal leaseReward = gbUTGRate.multiply(rentCapacity).multiply(priceRate).multiply(priceIndex)
				.multiply(bandwidthIndex.add(storageIndex)).multiply(new BigDecimal("0.8"));
		return leaseReward.divide(BigDecimal.valueOf(10).pow(18)).multiply(rentDays);
	}
	
	@Override
	public ResultMap<?> updateStorageSpaceAttach(String device_addr, String text, String pic, String address, String sign) {
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null) {
			return ResultMap.getFailureResult("invaild device address");
		}
		if(space.getAttachTime()!=null && (System.currentTimeMillis() - space.getAttachTime().getTime() <24*3600*1000L)){
			return ResultMap.getFailureResult("You have modified this information today");
	//		logger.warn("You have modified this information today");
		}				
		String origdata = device_addr + "&" + text + "&" + pic;		
		address = Constants.truncPrefix(address);
		try {			
			vaildSignData(sign,origdata,address);			
		} catch (Exception e) {
			return ResultMap.getFailureResult(e.getMessage());
		}
		String picmd5 = StringUtil.isEmpty(pic) ? "" : DigestUtils.md5DigestAsHex(pic.getBytes());
		space.setAttachText(text);
		space.setAttachPic(pic);		
		space.setAttachPicmd5(picmd5);
		space.setAttachTime(new Date());
		storageSpaceMapper.updateSpaceAttach(space);		
		return ResultMap.getSuccessfulResult("ok");
	}
	
	@Override
	public ResultMap<?> updateStorageRentAttach(String rent_hash, String text, String pic, String address, String sign) {
		StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
		if (rent == null) {
			return ResultMap.getFailureResult("invaild rent hash");
		}
		if(rent.getAttachTime()!=null && (System.currentTimeMillis() - rent.getAttachTime().getTime() <24*3600*1000L)){
			return ResultMap.getFailureResult("You have modified this information today");
	//		logger.warn("You have modified this information today");
		}				
		String origdata = rent_hash + "&" + text + "&" + pic;		
		address = Constants.truncPrefix(address);
		try {			
			vaildSignData(sign,origdata,address);			
		} catch (Exception e) {
			return ResultMap.getFailureResult(e.getMessage());
		}
		String picmd5 = StringUtil.isEmpty(pic) ? "" : DigestUtils.md5DigestAsHex(pic.getBytes());
		rent.setAttachText(text);
		rent.setAttachPic(pic);		
		rent.setAttachPicmd5(picmd5);
		rent.setAttachTime(new Date());
		storageRentMapper.updateRentAttach(rent);		
		return ResultMap.getSuccessfulResult("ok");
	}

	@SuppressWarnings("serial")
	private static Map<String,String> contentTypeMap =  new HashMap<String,String>(){{
		put("image/gif",".gif");
		put("image/x-icon",".ico");
		put("image/jpeg",".jpg");
		put("image/png",".png");
		put("text/plain",".txt");
		put("text/html",".html");
		put("text/xml",".xml");
		put("text/csv",".csv");
		put("application/json",".json");
		put("application/xml",".xml");
		put("application/javascript",".js");
		put("application/pdf",".pdf");
	}}; 
	
	@Override
	public void getStorageSpaceAttachFile(HttpServletRequest request, HttpServletResponse response, String device_addr) {
		StorageSpace space = storageSpaceMapper.getSpaceInfo(device_addr);
		if (space == null){ 
			logger.info("invaild device address");
			return;
		}		
		String pic = space.getAttachPic();
		String picmd5 = space.getAttachPicmd5();
		if(StringUtil.isEmpty(pic)||StringUtil.isEmpty(picmd5)){
			logger.info("rent "+device_addr+" has no attach data");
			return;
		}
		
		getAttachFile(response,pic,picmd5,device_addr);		
	}
	
	@Override
	public void getStorageRentAttachFile(HttpServletRequest request, HttpServletResponse response, String rent_hash) {
		StorageRent rent = storageRentMapper.getRentInfo(rent_hash);
		if (rent == null){ 
			logger.info("invaild rent hash");
			return;
		}		
		String pic = rent.getAttachPic();
		String picmd5 = rent.getAttachPicmd5();
		if(StringUtil.isEmpty(pic)||StringUtil.isEmpty(picmd5)){
			logger.info("rent "+rent_hash+" has no attach data");
			return;
		}
		getAttachFile(response,pic,picmd5,rent_hash);
	}
	
	
	
	private void vaildSignData(String sign,String origdata,String address){		
		try {
			byte[] sig = Numeric.hexStringToByteArray(sign);
			byte[] sr = new byte[32];
			System.arraycopy(sig, 0, sr, 0, 32);
			byte[] ss = new byte[32];
			System.arraycopy(sig, 32, ss, 0, 32);
			byte[] sv = new byte[1];
			System.arraycopy(sig, 64, sv, 0, 1);
			BigInteger v = new BigInteger(sv);
			SignatureData signatureData = new SignatureData(v.toByteArray(), sr, ss);
			BigInteger publickey = Sign.signedPrefixedMessageToKey(origdata.getBytes(StandardCharsets.UTF_8), signatureData);
			String addressV = Keys.getAddress(publickey);
			if (!address.equals(addressV)) {
				logger.warn("Vaildate sign origdata data = " + origdata+" , sign = "+sign);
				logger.warn("Vaildate sign input address " + address + " is not equal with sign address " + addressV);
				throw new RuntimeException("vaildate sign failed");
			}
		} catch (Exception e) {
			logger.warn("updateStorageRentAttach vaildate sign error:", e);
			throw new RuntimeException("vaildate sign error");
		}
	}
	
	private void getAttachFile(HttpServletResponse response,String pic,String picmd5,String filename){
		String contentType = null;
		String extension = null;
		if(pic.startsWith("data:")){
			contentType = pic.substring(5,pic.indexOf(";"));
			extension = contentTypeMap.get(contentType);
		}
		if(pic.contains("base64,"))
			pic = pic.substring(pic.indexOf("base64,")+7);
		String rootpath = System.getProperty("user.dir");	//getClass().getResource("/").getPath();
		rootpath = rootpath + "/data/attach/";				//	/data/explorerapi/data/	
		String filepath = rootpath + picmd5;		
		if(extension!=null){
			filepath += extension;
			filename += extension;
		}
		InputStream in = null;
		BufferedOutputStream out = null;
		try{
			File file = new File(filepath);
			if(!file.exists()){				
				File dir = file.getParentFile();
				if(!dir.exists())
					dir.mkdirs();
				file.createNewFile();
				FileUtil.decodeFileBase64(pic, file);				
			}			
			in = new BufferedInputStream(new FileInputStream(file));			
			response.addHeader("Content-Disposition", "attachment;filename="+ filename);
			if(contentType!=null)
				response.setContentType("contentType");
			out = new BufferedOutputStream(response.getOutputStream());
			int len = 0;
			while ((len = in.read()) != -1) {
				out.write(len);
				out.flush();
			}			
		} catch (Exception e) {
			log.warn("Write " + filename + " attach file " + picmd5 + " failed:", e);			
		}finally{
			if(out!=null)
				try { out.close(); } catch (IOException e) {}
			if(in!=null)
				try { in.close(); } catch (IOException e) {}			
		}
	}	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<?>> getSNListBySpHash(StorageQueryForm storageQueryForm) {		
		Page page = storageQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        storageQueryForm.setCurrent(pageCurrent);
        List<?> list = storageSpaceMapper.getSnListBySpHash(storageQueryForm.getSpHash(),storageQueryForm.getStatus());
		long total = storageSpaceMapper.getSnListBySpHashCount(storageQueryForm.getSpHash());
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}
	
}
