package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.BlockMapper;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.NodeLjRewardMapper;
import com.imooc.mapper.StoragePoolMapper;
import com.imooc.mapper.TransferMinerMapper;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.Form.*;

import com.imooc.pojo.vo.TransferMinerVo;
import com.imooc.service.TransferMinerService;
import com.imooc.utils.Constants;
import com.imooc.utils.HttpUtils;
import com.imooc.utils.ResultMap;
import com.imooc.utils.Web3jUtils;

//import jnr.ffi.annotations.In;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.AlienSnapshot;
import org.web3j.utils.Numeric;

@Service
public class TransferServiceImpl implements TransferMinerService {
    @Autowired
    private TransferMinerMapper transferMinerMapper;

    @Autowired
    private NodeExitMapper nodeExitMapper;

    @Autowired
    private UtgStorageMinerMapper utgStorageMinerMapper;
    
    @Autowired
    private BlockMapper blockMapper;
    @Autowired
    private StoragePoolMapper poolMapper;
    private Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

    @Value("${ipaddress}")
    private String ipaddress;

    @Value("${teamlock}")
    private String teamlock;


    @Value("${lockFeeNumber}")
    private String lockFeeNumber;

    @Value("${dayOneNumber}")
    private Integer dayOneNumber;

    private  String profitUrl;
    @Autowired
	private NodeLjRewardMapper ljRdDao;
    public ResultMap getMinerInfoForAddress(TransferMinerForm transferMinerForm) {
        Integer[] types = transferMinerForm.getType();
        Long id = transferMinerForm.getId();
        List<TransferMiner> listInfo = transferMinerMapper.getAllMinerForInfo(id, types);
        return ResultMap.getSuccessfulResult(listInfo);
    }

    @Override
    public ResultMap getAllMinerInfoWallet(MinerQueryForm minerQueryForm) {
        Integer[] types = minerQueryForm.getType();
        String address = minerQueryForm.getAddress();
        List<TransferMiner> listInfo = transferMinerMapper.getAllMinerInfo(address, types);
        return ResultMap.getSuccessfulResult(listInfo);
    }

    @Override
    public ResultMap<Page<TransferMiner>> getLockAndPledgeInfo(LockUpTransferForm lockUpTransferForm) {
        Page page = lockUpTransferForm.newFormPage();
        Integer type = lockUpTransferForm.getType();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("type", type);
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        lockUpTransferForm.setCurrent(pageCurrent);
        lockUpTransferForm.setSize(pageSize);
        List<TransferMiner> listInfos = transferMinerMapper.getPledgeInfo(lockUpTransferForm);
        page.setRecords(listInfos);
        Long total=transferMinerMapper.getPledgeInfoCount(lockUpTransferForm);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap<Page<TransferMinerVo>> getLockUtgMinerInfo(BlockQueryForm blockQueryForm)throws Exception {
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        String address=blockQueryForm.getAddress();
        List<TransferMinerVo> listInfos =null;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        Long total=0L;
        listInfos =transferMinerMapper.getMinerInfoForUtg(blockQueryForm);
        total=transferMinerMapper.getTotalMinerForUtg(blockQueryForm);
        Long dayNumber=Long.valueOf(dayOneNumber);
        Long dayNumberThree =30*dayNumber;
        Long dayNumberSix =180*dayNumber;
        Long yearNumberSix=2190*dayNumber;
        Long yearNumberOne=365*dayNumber;
        page.setRecords(listInfos);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    public  ResultMap getUtgMinerLockSum(BlockQueryForm blockQueryForm) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        TransferMinerVo tm = transferMinerMapper.getMinerSum(blockQueryForm);
        if(tm!=null){
            resultMap.put("lockamount",tm.getTotalAmount());
            resultMap.put("releaseamount",tm.getReleaseamount());
        }
        if(blockQueryForm.getType()==1||blockQueryForm.getType()==3){
            UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
            queryForm.setNode_address(blockQueryForm.getAddress());
            UtgNodeMiner pre_node = nodeExitMapper.getNode(queryForm);
            if(null!=pre_node){
                resultMap.put("minerAddress",pre_node.getNode_address());
                resultMap.put("revenueAddress",pre_node.getRevenue_address());
            }
        }else{
            UtgStorageMiner miner = utgStorageMinerMapper.getSingleMiner(blockQueryForm.getAddress());
            if(null!=miner){
                resultMap.put("minerAddress",miner.getMiner_addr());
                resultMap.put("revenueAddress",miner.getRevenue_address());
            }
        }
        return ResultMap.getSuccessfulResult(resultMap);

    }

    @Override
    public ResultMap<Page<TransferMinerVo>> getlockUtgMinersByRevAddress(BlockQueryForm blockQueryForm)throws Exception {
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        List<TransferMinerVo> listInfos =transferMinerMapper.getLockUtgMinersByRevAddress(blockQueryForm);
        Long total=transferMinerMapper.getTotalLockUtgMinersByRevAddress(blockQueryForm);
        page.setRecords(listInfos);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }
    @Override
    public ResultMap getLockSummaryByRevAddress(BlockQueryForm blockQueryForm) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        TransferMinerVo vo = transferMinerMapper.getLockSummaryByRevAddress(blockQueryForm);
        if(vo!=null){
            resultMap.put("totalamount",vo.getTotalAmount());
            resultMap.put("releaseamount",vo.getReleaseamount());
            resultMap.put("leftamount",vo.getLeftAmount());
        }else{
            resultMap.put("totalamount",0);
            resultMap.put("releaseamount",0);
            resultMap.put("leftamount",0);
        }
        blockQueryForm.setType(blockQueryForm.getType()+1);
        resultMap.put("releasecount",transferMinerMapper.getTotalRealease(blockQueryForm));
        return ResultMap.getSuccessfulResult(resultMap);
    }


    @Override
    public ResultMap<Page<TransferMiner>> getOverData(BlockQueryForm blockQueryForm) {
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        List<TransferMiner> list=transferMinerMapper.getOverData(blockQueryForm);
        if(list.size()>0 &&list !=null){
            for(TransferMiner transferMiner:list){
                String value=getProfitVal(transferMiner.getTxHash());
                transferMiner.setProfitValReward(new BigDecimal(value));//]
                transferMiner.setValue(getTotalValueForTxHash(transferMiner.getTxHash()));
            }
            page.setRecords(list);
        }
        Long total=transferMinerMapper.getTotalInfo(blockQueryForm);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    public BigDecimal getTotalValueForTxHash(String txHash){
         BigDecimal value = transferMinerMapper.getTotalValueForTxHash(txHash);
         return value;
    }


    public String getProfitVal(String txHash) {
        long timeStamp = System.currentTimeMillis();
        String url =profitUrl+ "random" + "=" + timeStamp;
        JSONObject json = new JSONObject();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("drawhash",txHash);
        json.put("data", paramMap);
        String param = json.toString();
        String result = HttpUtils.HttpPostWithJson(url, param);
        if (result.equals("0.0")) {
            return "0.0";
        }
        JSONObject jsons = JSONObject.fromObject(result);
        String value;
        if (jsons.getString("result").equals("0")) {
            String data = jsons.getString("data");
            JSONObject json1 = JSONObject.fromObject(data);
            value = json1.getString("profitval");
            return value;
        } else {
            return "0.0";
        }

    }

    @Override
    public ResultMap getPledgeExit() throws Exception{
        String admin="524e3758e1eec73c9d970c1b528ddbb68852ceae";
        admin="000000000000000000000000"+admin;
        admin="0x662e5ba7"+admin;
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        org.web3j.protocol.core.methods.request.Transaction transaction =
                new org.web3j.protocol.core.methods.request.Transaction(null, null, null, null, "0xDA477d4763e1DC8c1672697C37C2aE4eB5415F37", null, admin);
        BigInteger number = new BigInteger("3210");
        String result=web3j.ethCall(transaction,DefaultBlockParameterName.LATEST).send().getResult();
        String rs="0x000000000000000000000000000000000000000000000000000000003B9ACA000000000000000000000000000000000000000000000000000000000005F5E10000000000000000000000000000000000000000000000000000000000055D4A800000000000000000000000000000000000000000000000000000000000000064000000000000000000000000000000000000000000000000000000000000001e0000000000000000000000000000000000000000000000000000000000000064000000000000000000000000000000000000000000000000000000000000000a";
        String r1 = rs.substring(2, 66);
        r1="0x"+r1;
        logger.info("r1"+r1);
        BigDecimal r11=convertToBigDecimal(r1);
        String r2=rs.substring(67,130);
        r2="0x"+r2;
        BigDecimal r21=convertToBigDecimal(r2);
        String r3=rs.substring(131,194);
        r3="0x"+r3;
        BigDecimal r31=convertToBigDecimal(r3);
        String r4=rs.substring(195,258);
        r4="0x"+r4;
        String r5=rs.substring(259,322);
        r5="0x"+r5;
        String r6 =rs.substring(323,386);
        r6="0x"+r6;
        String r7 =rs.substring(387,450);
        r7="0x"+r7;
        BigDecimal r41=convertToBigDecimal(r4);
        BigDecimal r51=convertToBigDecimal(r5);
        BigDecimal r61=convertToBigDecimal(r6);
        BigDecimal r71=convertToBigDecimal(r7);
        saveNodeExit(r11,r21,r31,r41,r51,r61,r71);
        return ResultMap.getSuccessfulResult(result);
    }

    private BigDecimal convertToBigDecimal(String value) {
        if (null != value)
            return new BigDecimal(Numeric.decodeQuantity(value));
        return null;
    }

    public Integer getLockParam() throws Exception {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
       // String tramlock=teamlock;
        org.web3j.protocol.core.methods.request.Transaction transaction = new org.web3j.protocol.core.methods.request.Transaction(null, null, null, null, teamlock, null, "0x05216523");
        String result = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send().getResult();
        String lockTime = result.substring(2, 66);
        String releseTime = result.substring(67, 130);
        Integer lock = Integer.parseInt(lockTime, 16);
        Integer relese = Integer.parseInt(releseTime, 16);
        Integer locks = lock + relese;
        return locks;
    }



    public Long getPeriod() throws Exception {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        int blockNumber = web3j.ethBlockNumber().send().getBlockNumber().intValue();
        AlienSnapshot.Snapshot snap = web3j.alientSnapshotByNumber(blockNumber).send().getSnapshot();
        Long period = snap.getPeriod().longValue();
        return period;
    }

    private void saveNodeExit(BigDecimal r11,BigDecimal r21,BigDecimal r31,BigDecimal r41,BigDecimal r51,BigDecimal r61,BigDecimal r71){
        NodeExit nodeExit = new NodeExit();
        nodeExit.setAddressName("");
        nodeExit.setTimeStamp(new Date());
        nodeExit.setPledgeAmount(r11);
        nodeExit.setDeductionAmount(r21);
        nodeExit.setTractAmount(r31);
        nodeExit.setLockStartNumber(r41.longValue());
        nodeExit.setLockNumber(r51.longValue());
        nodeExit.setReleaseNumber(r61.longValue());
        nodeExit.setReleaseInterval(r71.longValue());
        nodeExitMapper.insert(nodeExit);
    }

    @Override
    public ResultMap<Page<Transaction>> getAllTotalForReceive(String address) {
        BigDecimal totalValue= transferMinerMapper.getAllTotalForReceive(address);
        return ResultMap.getSuccessfulResult(totalValue);
    }

    @Override
    public ResultMap<Page<TransferMinerVo>> getLockUtgMinerInfoWallet(BlockQueryForm blockQueryForm) throws Exception{
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        String address=blockQueryForm.getAddress();
        List<TransferMinerVo> listInfos =null;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        Long total=0L;
        if(address==null ||address.equals("")){
           logger.info("");
        }else{
            listInfos =transferMinerMapper.getMinerInfoUtgSerachForWallet(blockQueryForm);
            total= transferMinerMapper.getTotalMinerSerachForWallet(blockQueryForm);
        }
        Long dayNumber=Long.valueOf(dayOneNumber);
        Long dayNumberThree =30*dayNumber;
        Long dayNumberSix =180*dayNumber;
        page.setRecords(listInfos);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    
    
    
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultMap<Page<TransferMinerVo>> getRewardList(BlockQueryForm blockQueryForm) {
    	//last 24h query
    	if(blockQueryForm.getLast24()!=null && blockQueryForm.getLast24()) {
    		NodeLjQueryForm ljForm=new NodeLjQueryForm();
    		long totalBlockNumber = blockMapper.getLeatestBlockNumber();
			long startBlock = totalBlockNumber - dayOneNumber;
			blockQueryForm.setStartBlock(startBlock);
			ljForm.setBlockNumber(startBlock);
    		Page page = ljForm.newFormPage();
			Long pageSize = page.getSize();
			Long pageCurrent = page.getCurrent();
			pageCurrent = (pageCurrent - 1) * pageSize;
			ljForm.setCurrent(pageCurrent);
			List<NodeReward> list = ljRdDao.getPageList(ljForm);
			List<TransferMinerVo> tfvlist=new ArrayList<>();
			if(list!=null && list.size()>0) {
				for(NodeReward reward:list) {
					TransferMinerVo vo=new TransferMinerVo();
					vo.setAddress(reward.getNodeAddress());
					vo.setRevenueaddress(reward.getRevenueAddress());
					vo.setBlockNumber(reward.getBlockNumber());
					vo.setLockNumber(vo.getBlockNumber());
					vo.setLockNumHeight(vo.getBlockNumber());
//					long day=(totalBlockNumber-reward.getBlockNumber())/8640;
					vo.setReleaseHeigth(vo.getBlockNumber()+210*dayOneNumber);
					vo.setTotalAmount(reward.getRewardAmount());
					if(reward.getRewardType()==3||reward.getRewardType()==6) {
						vo.setType(1);
					}else if(reward.getRewardType()==5) {
						vo.setType(9);
					}else if(reward.getRewardType()==4||reward.getRewardType()==13) {
						vo.setType(5);
					}else if(reward.getRewardType()==8||reward.getRewardType()==9) {
						vo.setType(5);
					}
					tfvlist.add(vo);
				}
			}
			long total = ljRdDao.getTotal(ljForm);
			page.setRecords(tfvlist);
			page.setTotal(total);
			return ResultMap.getSuccessfulResult(page);
    	}
    	boolean isedit = false;
    	if(blockQueryForm.getTypes() != null) {
    		for (Integer type : blockQueryForm.getTypes()) {
				if (type.intValue()>=21 && type.intValue()<=28) {
					isedit=true;
					
				}
			}
    	}
		if (blockQueryForm.getAddress() != null ) {
			
			if(isedit && blockQueryForm.getAddress().length()>45) {
				if(blockQueryForm.getAddress().startsWith("ux")) {
					blockQueryForm.setAddress(Constants.prefixAddress(blockQueryForm.getAddress().substring(26)));
				}else {
					blockQueryForm.setAddress(Constants.prefixAddress(blockQueryForm.getAddress().substring(24)));
					
				}
				}
		}
		Page page = blockQueryForm.newFormPage();
		Long pageSize = page.getSize();
		Long pageCurrent = page.getCurrent();
		pageCurrent = (pageCurrent - 1) * pageSize;		
		blockQueryForm.setCurrent(pageCurrent);
		if(blockQueryForm.getLast24()!=null && blockQueryForm.getLast24()==true){
			long totalBlockNumber = blockMapper.getLeatestBlockNumber();
			long startBlock = totalBlockNumber - dayOneNumber;
			blockQueryForm.setStartBlock(startBlock);
		}
		boolean release = false;
		if(blockQueryForm.getType()!=null && blockQueryForm.getType()%2==0)
			release = true;
		else if(blockQueryForm.getTypes()!=null && blockQueryForm.getTypes().length>0 && blockQueryForm.getTypes()[0]%2==0)
			release = true;
		blockQueryForm.setTable(release ? "transfer_miner_release":"transfer_miner");
		List<TransferMinerVo> list = transferMinerMapper.getStorageRewardList(blockQueryForm);
		long total = transferMinerMapper.getStorageRewardTotal(blockQueryForm);
		if(isedit) {
			Map<String,String>  poolMap=getPoolMap();
			for(TransferMinerVo vo:list) {
				String spHash=poolMap.get(vo.getAddress());
				if(spHash!=null&&spHash.length()>0) {
					vo.setAddress(spHash);
				}
				if(vo.getPledgeAddress()!=null&&poolMap.containsKey(vo.getPledgeAddress())) {
					vo.setPledgeAddress(poolMap.get(vo.getPledgeAddress()));
				}
			}
		}
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);		
	}
	
    public Map<String,String> getPoolMap(){
    	Map<String,String>  poolMap=new HashMap<>();
    	List<StoragePool>  list=poolMapper.getStoragePoolList(new StoragePoolForm());
    	if(list!=null) {
    		for(StoragePool pool : list) {
    			poolMap.put(pool.getSpAddr(), pool.getHash());
    		}
    	}
    	return poolMap;
    }
	
	@Override
	public ResultMap<Map> getRewardStat(BlockQueryForm blockQueryForm) {	
		if(blockQueryForm.getLast24()!=null && blockQueryForm.getLast24()==true){
			long totalBlockNumber = blockMapper.getLeatestBlockNumber();
			long startBlock = totalBlockNumber - dayOneNumber;
			blockQueryForm.setStartBlock(startBlock);
			List<NodeReward> rewardList=ljRdDao.getNodeRewardStat(startBlock);
			Map<String,Object> map = new HashMap<String,Object>();
			if(rewardList!=null && rewardList.size()>0) {
				BigDecimal totalAmount=BigDecimal.ZERO;
				BigDecimal poSAmount=BigDecimal.ZERO;
				BigDecimal GrantAmount=BigDecimal.ZERO;
				BigDecimal ServiceAmount=BigDecimal.ZERO;
				for(NodeReward reward:rewardList) {
					totalAmount=totalAmount.add(reward.getRewardAmount());
					if("PoS".equals(reward.getNodeType())) {
						poSAmount=poSAmount.add(reward.getRewardAmount());
					}else {
						if(reward.getRewardType()==5) {
							GrantAmount=GrantAmount.add(reward.getRewardAmount());
						}else if(reward.getRewardType()==4||reward.getRewardType()==13) {
							ServiceAmount=ServiceAmount.add(reward.getRewardAmount());
						}else if(reward.getRewardType()==8||reward.getRewardType()==9) {
							ServiceAmount=ServiceAmount.add(reward.getRewardAmount());
						}
					}
				}
				int typs[]= {1,5,9};
				List<Map<String,Object>> typelist = new ArrayList<>();
				
				for(int type:typs) {
					BigDecimal tamont=poSAmount;
					if(type==9) {
						tamont=GrantAmount;
					}else if(type==5) {
						tamont=ServiceAmount;
					}
					Map<String,Object> item = new HashMap<>();
					item.put("type",type);
					item.put("totalAmount", tamont);
					item.put("releaseamount", BigDecimal.ZERO);
					item.put("burntamount", BigDecimal.ZERO);
					typelist.add(item);
				}
				
				map.put("totalAmount", totalAmount);
				map.put("releaseamount", BigDecimal.ZERO);
				map.put("burntamount", BigDecimal.ZERO);	
				map.put("typelist", typelist);
			}
			return ResultMap.getSuccessfulResult(map);	
		}		
		TransferMinerVo vo = transferMinerMapper.getMinerSum(blockQueryForm);		
		Map<String,Object> map = new HashMap<String,Object>();
		if(vo!=null){
			map.put("totalAmount", vo.getTotalAmount());
			map.put("releaseamount", vo.getReleaseamount());
			map.put("burntamount", vo.getBurntamount());			
			if(blockQueryForm.getBytype()==null || blockQueryForm.getBytype()==true){			
				List<TransferMinerVo> list = transferMinerMapper.getMinerTypeSum(blockQueryForm);
				List<Map<String,Object>> typelist = new ArrayList<>();
				for(TransferMinerVo tm : list){
					Map<String,Object> item = new HashMap<>();
					item.put("type", tm.getType());
					item.put("totalAmount", tm.getTotalAmount());
					item.put("releaseamount", tm.getReleaseamount());
					item.put("burntamount", tm.getBurntamount());
					typelist.add(item);
				}
				map.put("typelist", typelist);
			}
		}
		return ResultMap.getSuccessfulResult(map);		
	}


	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<TransferMinerVo> getRewardInfo(String id) {
		TransferMinerVo data = transferMinerMapper.getTransferMinerData(id);
		if(data!=null){			
			return ResultMap.getSuccessfulResult(data);
		}else{
			return ResultMap.getFailureResult("query no data");
		}
	}

}