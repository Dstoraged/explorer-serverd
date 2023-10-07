package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.DposVoterQueryForm;
import com.imooc.pojo.vo.*;
import com.imooc.service.AccountService;
import com.imooc.service.BlockService;
import com.imooc.service.util.TxDataParse;
import com.imooc.utils.*;
//import jnr.ffi.annotations.In;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BlockServiceImpl implements BlockService {

    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TransferMinerMapper transferMinerMapper;

    @Autowired
    private ApiConfigMapper apiConfigMapper;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private NodeExitMapper nodeExitMapper;
    @Autowired
    private GlobalConfigMapper  globalConfigMapper;

//    @Autowired
//    private DposVotesMapper dposVotesMapper;
    @Autowired
    private OverViewMapper  overMapper;
    @Value("${ipaddress}")
    private String ipaddress;

    @Value("${FROM_ADDRESS_HASH}")
    private String fromaddressHash;

    private String requestUrl;

    private String accessid;

    @Value("${accessjson}")
    private String accessjson;

    @Value("${key}")
    private String key;

    @Value("${contractaddress1}")
    private String contractaddress1;

    @Value("${contractaddress2}")
    private String contractaddress2;

    @Value("${contractaddress3}")
    private String contractaddress3;

    @Value("${addresspool}")
    private String addresspool;

    @Value("${mineraddress1}")
    private String mineraddress1;

    @Value("${mineraddress2}")
    private String mineraddress2;

    @Value("${mineraddress3}")
    private String mineraddress3;

    @Value("${blocktotal}")
    private String blocktotal;

    @Value("${year}")
    private String year;

    @Value("${yeartotal}")
    private String yeartotal;

    @Value("${dayOneNumber}")
    private   Long dayOneNumber;
    
    @Value("${allrewards}")
    private String allrewards;

    @Value("${round}")
    private long round;
    @Value("${epoch}")
    private long epoch;
    @Value("${perblocksecond}")
    private   int perblocksecond;//How many seconds is a block
    private static final String userPrikey = "pppp";

    private static final Logger logger = LoggerFactory.getLogger(BlockServiceImpl.class);

    @Override
    public void save(Blocks block) {

    }
    @Override
    public ResultMap getDataForUtg() {
        Map<String, Object> map = new HashMap<String, Object>();
        String bill = getAllBill();
        String utg = getAllUtg();
        String miner = getAllMiner();
        map.put("ActiveMiner", miner);
        map.put("ComputingPower", utg);
        map.put("TotalNetWorkTraffic", bill);
        return ResultMap.getSuccessfulResult(map);
    }
    public String getAllBill() {
//        long timeStamp = System.currentTimeMillis();
//        String url = requestUrl + "accessid" + "=" + accessid + "&" + "random" + "=" + timeStamp;
//        String md5Val = MD5.encodeByMD5(accessjson + timeStamp);
//        String hash = HmacsHa256.sha256_HMAC(md5Val, key);
//        JSONObject json = new JSONObject();
//        json.put("sig", hash);
//        String param = json.toString();
//        String result = HttpUtils.HttpPostWithJson(url, param);
//        if (result.equals("0.0")) {
//            return "0.0";
//        }
//        JSONObject jsons = JSONObject.fromObject(result);
//        String value;
//        String unit;
//        if (jsons.getString("result").equals("0")) {
//            String data = jsons.getString("data");
//            JSONObject json1 = JSONObject.fromObject(data);
//            unit = json1.getString("unit");
//            value = value + " ";
//            value = value + unit;
//            return value;
//        } else {
        return "0.0";
//        }

    }

    public String getAllUtg() {
//        long timeStamp = System.currentTimeMillis();
//        String url = requestUrl + "accessid" + "=" + accessid + "&" + "random" + "=" + timeStamp;
//        String md5Val = MD5.encodeByMD5(accessjson + timeStamp);
//        String hash = HmacsHa256.sha256_HMAC(md5Val, key);
//        JSONObject json = new JSONObject();
//        json.put("sig", hash);
//        String param = json.toString();
//        String result = HttpUtils.HttpPostWithJson(url, param);
//        if (result.equals("0.0")) {
//            return "0.0";
//        }
//        JSONObject jsons = JSONObject.fromObject(result);
//        String value;
//        if (jsons.getString("result").equals("0")) {
//            String data = jsons.getString("data");
//            JSONObject json1 = JSONObject.fromObject(data);
//            return value;
//        } else {
        return "0.0";
//        }

    }


    public String getAllMiner() {
//        long timeStamp = System.currentTimeMillis();
//        String url = requestUrl + "accessid" + "=" + accessid + "&" + "random" + "=" + timeStamp;
//        String md5Val = MD5.encodeByMD5(accessjson + timeStamp);
//        String hash = HmacsHa256.sha256_HMAC(md5Val, key);
//        JSONObject json = new JSONObject();
//        json.put("type", "miner");
//        json.put("sig", hash);
//        String param = json.toString();
//        String result = HttpUtils.HttpPostWithJson(url, param);
//        if (result.equals("0.0")) {
//            return "0.0";
//        }
//        JSONObject jsons = JSONObject.fromObject(result);
//        String value;
//        if (jsons.getString("result").equals("0")) {
//            String data = jsons.getString("data");
//            JSONObject json1 = JSONObject.fromObject(data);
//            value = json1.getString("minernum");
//            return value;
//        } else {
        return "0.0";
//        }

    }
    public   ResultMap getBlockNumber(){
        Map<String, Object> map = new HashMap<String, Object>();
        long totalBlockNumber = blockMapper.getLeatestBlockNumber();
        map.put("totalBlockNumber", totalBlockNumber);//
        try {
        String utgv2numberStr=globalConfigMapper.getTypeValue("utgv2number");
        Long utgv2number=utgv2numberStr==null?0:Long.parseLong(utgv2numberStr);
        map.put("utgv2number", utgv2number);
        }catch (Exception e) {
			logger.error("get utgv2number error",e);
		}
        return ResultMap.getSuccessfulResult(map);
    }

    @Override
    public ResultMap getDatas() {
//        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        try {
            Map<String, Object> map = new HashMap<String, Object>();
//            Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
            long totalBlockNumber = blockMapper.getLeatestBlockNumber();
//            String fromHash = fromaddressHash.trim();
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("txType","0");
//            long transactionCount = transactionMapper.getTotalTransactionCount(paramsMap);
//            BigInteger balance2 = web3j.ethGetBalance(contractaddress2, DefaultBlockParameterName.LATEST).send().getBalance();
//            BigInteger balance3 = web3j.ethGetBalance(contractaddress3, DefaultBlockParameterName.LATEST).send().getBalance();
//            long nowNumber = web3j.ethBlockNumber().send().getBlockNumber().longValue();
//            long avgTime=20L;
            map.put("totalBlockNumber", totalBlockNumber);
            map.put("nextElectTime", calNextElectTime(totalBlockNumber));        	 
       //     map.put("destrNum", getAddr0Balance());
            map.put("destrNum", transactionMapper.getAddr0Balance(Constants.addressZero));
            long blockYesetoday = totalBlockNumber - dayOneNumber;            
            BlockOverView  viewing=getOverView(blockYesetoday);            
            map.put("pledgeNum", viewing.getPladgenum());
            map.put("lockNum", viewing.getLocknum());
            map.put("utg24", viewing.getTotalutg());
            
            map.put("bandWidthSize", viewing.getBandwidth());
            map.put("utgToGb",viewing.getUtgtogb());

            return ResultMap.getSuccessfulResult(map);
        } catch (Exception e) {
            logger.error("error",e);
        }
        return ResultMap.getFailureResult("");
    }

    public  BigDecimal getMinerNumber(Long nowNumber){
        try {
            double x = 210000d/420000;
            BigDecimal total = new BigDecimal(x* Math.pow(1-0.04,nowNumber/420000)) ;
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getMinerNumber error",e);
            return new BigDecimal("0");
        }
    }

    private BlockOverView  getOverView(long blockYesetoday){
        BlockOverView  info=null;
        try{
            info= overMapper.getFwMinerData();
            info.setBandwidth(BigDecimal.ZERO);
            BlockOverView  nodeinfo=  overMapper.getNodeMinerData();
            if(nodeinfo!=null){
                info.setPladgenum(nodeinfo.getNodepledgenum().add(info.getFwpledgenum()));	
            }
            BlockOverView  lockinfo=  overMapper.getCurrLockData();
            if(lockinfo!=null){
                info.setLocknum(lockinfo.getLocknum());
            }
            /*BlockOverView  dfninfo=overMapper.getutgtogbData();
            if(dfninfo!=null){
                info.setUtgtogb(dfninfo.getUtgtogb());
                info.setTotalutg(dfninfo.getTotalutg());
            }*/
            /*BlockOverView  dfninfo=overMapper.getutgtogbData2();
            if(dfninfo!=null){
            	BigDecimal previewutg = dfninfo.getTotalutg()==null ? BigDecimal.ZERO : dfninfo.getTotalutg();
            	BigDecimal totalutg = info.getLocknum()==null ? BigDecimal.ZERO : info.getLocknum();
            	BigDecimal dayutg = totalutg.subtract(previewutg); 
            	if(dayutg.compareTo(BigDecimal.ZERO)<0)
            		dayutg = BigDecimal.ZERO;
            	info.setTotalutg(dayutg);            	
            }*/
            
            BlockOverView  dfninfo=overMapper.getutg24(blockYesetoday);
            if(dfninfo!=null){
            	info.setTotalutg(dfninfo.getTotalutg());            	
            }
            
            BlockOverView storageinfo = overMapper.getStorageData();
            if(storageinfo!=null){
            	info.setBandwidth(storageinfo.getBandwidth());
            	info.setUtgtogb(storageinfo.getUtgtogb());
            	BigDecimal pledge_amount = storageinfo.getPladgenum();
            	if(pledge_amount!=null){
            		BigDecimal pladgenum = info.getPladgenum();
            		if(pladgenum==null)
            			pladgenum = BigDecimal.ZERO;
            		pladgenum = pladgenum.add(pledge_amount);
            		info.setPladgenum(pladgenum);
            	}            	
            }
            
           
            return  info;
        }catch (Exception e){
            e.printStackTrace();
               logger.error("get overview error",e);
        }
        return new BlockOverView();

    }


    private String calNextElectTime(long totalBlockNumber){
        StringBuffer selectTime = new StringBuffer();
        try {
            if(totalBlockNumber<=epoch){
                selectTime.append(epoch);
            }else{
                selectTime.append((totalBlockNumber+(epoch-totalBlockNumber%epoch)));
            }
    //    logger.info("totalBlockNumber="+totalBlockNumber+" epoch="+epoch+"  selectTime="+selectTime.toString());
//            long day = between / (24 * 3600);
//            long hour = between % (24 * 3600) / 3600;
//            long minute = between % 3600 / 60;
//            long second = between % 60;
//
//            if (day > 0) {
//                selectTime.append(day + " days ");
//                selectTime.append(hour + " hours ");
//                selectTime.append(minute + " minutes ");
//                if (second > 0) {
//                    selectTime.append(second + " seconds");
//                }
//            } else {
//                if (hour > 0) {
//                    selectTime.append(hour + " hours ");
//                    selectTime.append(minute + " minutes ");
//                    if (second > 0) {
//                        selectTime.append(second + " seconds");
//                    }
//                } else {
//                    if (minute > 0) {
//                        selectTime.append(minute + " minutes ");
//                        if (second > 0) {
//                            selectTime.append(second + " seconds");
//                        }
//                    } else {
//                        selectTime.append(second + " seconds");
//                    }
//
//                }
//            }
            return selectTime.toString();
        }catch (Exception e){
            logger.error("Error calculating election time",e);
        }
        return selectTime.toString();
    }
    private  Date getNextWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(date));
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }
    private  Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    private BigDecimal getAddr0Balance() {
   //     BigDecimal bal0 =transactionMapper.getAddr0Balance();
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        EthGetBalance ethGetBalance = null;
        BigDecimal bal0 = BigDecimal.ZERO;
		try {
			ethGetBalance = web3j.ethGetBalance(Constants.addressZero, DefaultBlockParameterName.LATEST).send();
			bal0 = new BigDecimal(ethGetBalance.getBalance());
		} catch (Exception e) {
			logger.warn("ethGetBalance 0 address ethGetBalance:"+ethGetBalance+",error:"+e.getMessage());
		}
        return bal0;
    }
    
    public BigDecimal getTotalValue() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        BigDecimal totalValue = transactionMapper.getTotalValues(paramMap);
        return totalValue;
    }

    public BigDecimal getYesterDayValue() {
        String startTime = DateUtil.getFormatDateTime(DateUtil.getBeginDayOfYesterday(), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.getFormatDateTime(DateUtil.getEndDayOfYesterDay(), "yyyy-MM-dd HH:mm:ss");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        BigDecimal yesterTotal = transactionMapper.getTotalValues(paramMap);
        return yesterTotal;
    }

    public Long getNewAccount() {
        String startTime = DateUtil.getFormatDateTime(DateUtil.getBeginDayOfYesterday(), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.getFormatDateTime(DateUtil.getEndDayOfYesterDay(), "yyyy-MM-dd HH:mm:ss");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        Long count = accountMapper.getNewAddress(paramMap);
        return count;
    }

    public BigDecimal getSendReward() {
        Integer[] type = {5};
        BigDecimal reard = transferMinerMapper.getReward(type);
        if (null == reard) {
            reard = new BigDecimal("0.0");
        }
        return reard;
    }

    @Override
    public ResultMap<Page<BlocksVo>> pageList(BlockQueryForm blockQueryForm) {
        BigDecimal b = new BigDecimal("0");
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        Long isBurnRecord= blockQueryForm.getBurned()!=null&&blockQueryForm.getBurned()?1l:0;
        Long minNumber =blockQueryForm.getBlockNumber();
        long total = blockMapper.getTotalCount(blockQueryForm.getAddress(),blockQueryForm.getMineraddress(),isBurnRecord,minNumber);
        long startNumber;
        if (pageCurrent == 1) {
            startNumber = total - pageSize;
        } else {
            startNumber = total - (pageCurrent * pageSize);
        }
        pageCurrent = (pageCurrent - 1) * pageSize;
        List<BlocksVo> list = blockMapper.pageList(pageCurrent, pageSize, startNumber,blockQueryForm.getAddress(),blockQueryForm.getMineraddress(),isBurnRecord,minNumber);
        if (list.size() == 0) {
            return ResultMap.getSuccessfulResult(null);
        }
        if (list.size() > 0 || list != null) {
            for (BlocksVo bkk : list) {
                long number = bkk.getBlockNumber();
                String fromHash = fromaddressHash.trim();
                long count = transactionMapper.getTotalByBlockNumber(number, fromHash);
                if (count == 0) {
                    bkk.setAvgGasPrice(0L);
                } else {
                    long acgGasPrice = transactionMapper.getAvgPrice(number);
                    bkk.setAvgGasPrice(acgGasPrice);
                }
                if (bkk.getReward() == null) {
                    bkk.setReward(b);
                }
            }
            page.setRecords(list);
            page.setTotal(total);
            return ResultMap.getSuccessfulResult(page);
        } else {
            return ResultMap.getSuccessfulResult(null);
        }
    }

    @Override
    public ResultMap getBlockInfoByBlockNumber(Long blockNumber) {
        List<BlocksVo> listInfo = blockMapper.getBlockInfoByNumber(blockNumber);
        if (listInfo.size() == 0) {
            return ResultMap.getSuccessfulResult(null);
        }
        for (BlocksVo bkk : listInfo) {
            long number = bkk.getBlockNumber();
            if (bkk.getBlockTransactionCount() == null) {
                bkk.setBlockTransactionCount(0);
            } else {
                bkk.setBlockTransactionCount(bkk.getTxsCount());
            }
            String fromHash = fromaddressHash.trim();
            long count = transactionMapper.getTotalByBlockNumber(number, fromHash);
            if (count == 0) {
                bkk.setAvgGasPrice(0L);
            } else {
                long acgGasPrice = transactionMapper.getAvgPrice(number);
                bkk.setAvgGasPrice(acgGasPrice);
            }
            BlockQueryForm blockQueryForm = new BlockQueryForm();
            blockQueryForm.setType(1);
            blockQueryForm.setBlockNumber(blockNumber);
            TransferMinerVo tm = transferMinerMapper.getMinerSum(blockQueryForm);
            if(tm!=null){
                bkk.setLockamount(tm.getTotalAmount());
                bkk.setReleaseamount(tm.getReleaseamount());
            }
        }
        return ResultMap.getSuccessfulResult(listInfo);
    }

    public ResultMap getAllParamters(long types,String language) {
        List<ApiConfigView> list = apiConfigMapper.getAllConfig(types);
        if(language.equals("zh")||language=="zh"){
            list= apiConfigMapper.getAllConfig(types);
        }else{
            list= apiConfigMapper.getAllConfigForEn(types);
        }
        return ResultMap.getSuccessfulResult(list);
    }




    @Override
    public ResultMap<Page<Blocks>> getRewardVoterPageList(DposVoterQueryForm dposVoterQueryForm) {
        List<BlockRewardVo> listInfo = blockMapper.getAllRewardForaddress(dposVoterQueryForm);
        if(listInfo.size()>0 ||!listInfo.isEmpty()){
            return ResultMap.getSuccessfulResult(listInfo);
        }else{
            return ResultMap.getSuccessfulResult(null);
        }
    }

    /**
     * Obtain the pledge quantity per 1Mbps of the current storage mining pledge 4 intervals
     * @return
     */
    @Override
    public ResultMap getBwPlgeRange() {
        Map<String, Object> map = new HashMap<String, Object>();
        try{
            map.put("bw0_300",overMapper.getBwPdgRnge300()) ;
            map.put("bw301_800",overMapper.getBwPdgRnge380()) ;
            map.put("bw801_1500",overMapper.getBwPdgRnge815()) ;
            map.put("bw1500",overMapper.getBwPdgRnge1500()) ;
            return ResultMap.getSuccessfulResult(map);
        }catch (Exception e){
            logger.error("Obtain the pledge quantity per 1Mbps of the current storage mining pledge 4 intervals error ",e);
        }

        return ResultMap.getFailureResult("");
    }
    
	@Override
	public ResultMap<?> getAddr0Data(Long blockNumber) {
		 Map<String, Object> map = new HashMap<String, Object>();
	//	 BigDecimal balance = getAddr0Balance();
		 BigDecimal balance = accountService.getAddressBalance(Constants.addressZero);
		 BigDecimal txin = transactionMapper.getAddr0Txin(Constants.addressZero);
         BigDecimal blockBurn=blockNumber==null?BigDecimal.ZERO:  blockMapper.getBlocksGasBurn(blockNumber);         
		 BlockQueryForm blockQueryForm = new BlockQueryForm();
         blockQueryForm.setTypes(new Integer[]{1,3,5,7,9});
         blockQueryForm.setBurned(true);
         TransferMinerVo tm = transferMinerMapper.getMinerSum(blockQueryForm);         
         BigDecimal burned = (tm==null||tm.getBurntamount()==null)?BigDecimal.ZERO:tm.getBurntamount();
		 
         BigDecimal pledgeBurn = nodeExitMapper.getNodePledgeBurnedAmount();         
		 BigDecimal nontx = balance.subtract(txin).subtract(burned).subtract(pledgeBurn);
		 map.put("balance", balance);
		 map.put("txin", txin);
		 map.put("burned", burned);		 
		 map.put("nontx", nontx);
         map.put("blockBurn",blockBurn);
         map.put("pledgeBurn", pledgeBurn);
		 return ResultMap.getSuccessfulResult(map);		
	}

}