package com.imooc.service.util;

import com.imooc.enums.MinerStatusEnum;
import com.imooc.enums.NodeTypeEnum;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.GlobalConfigMapper;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.StorageSpaceMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.pojo.UtgStorageMiner;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.GlobalConfig;
import com.imooc.pojo.NodePledge;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.Transaction;
import com.imooc.utils.Constants;
import com.imooc.utils.UTGOperatorEnum;
import com.imooc.utils.SscOperatorEnum;
import com.imooc.utils.UfoPrefixEnum;
import lombok.extern.slf4j.Slf4j;

import org.web3j.protocol.core.methods.response.Log;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * (UTG/SSC)
 * @author zhc 2021-09-09 18:00
 */
@Slf4j
public class TxDataParse {
    private final static   String zeroA = "0000000000000000000000000000000000000000";
//    private static String address1 = "NX7A4539Ed8A0b8B4583EAd1e5a3F604e83419f502";
//    private static String address2 = "NX7A4539Ed8A0b8B4583EAd1e5a3F604e83419f502";
//    private static String address3 = "NX7A4539Ed8A0b8B4583EAd1e5a3F604e83419f502";
    private static final String candReqTopic = Constants.PRE+"61edf63329be99ab5b931ab93890ea08164175f1bce7446645ba4c1c7bdae3a8"; //Node pledge
    private static final String flwReqTopic = Constants.PRE+"041e56787332f2495a47171278fa0f1ddb21961f702d0ba53c2bb2c079ccd418";//Data mining pledge, claimed bandwidth
    private static final String pledgeExitTopic=Constants.PRE+"9489b96ebcb056332b79de467a2645c56a999089b730c99fead37b20420d58e7"; //Node exit, traffic mining exit
    private static final String bindTopic = Constants.PRE+"f061654231b0035280bd8dd06084a38aa871445d0b7311be8cc2605c5672a6e3"; //Binding, unbinding, replacement
//    private static final String exchTopic = Constants.PRE+"dd6398517e51250c7ea4c550bdbec4246ce3cd80eac986e8ebbbb0eda27dcf4c"; //ful exchange
    private static final String exchTopic = Constants.PRE+"1ebef91bab080007829976060bb3c203fd4d5b8395c552e10f5134e188428147";
    private static final String candPnshTopic = Constants.PRE+"d67fe14bb06aa8656e0e7c3230831d68e8ce49bb4a4f71448f98a998d2674621"; //Penalty make-up


    /**
     * Transaction input
     * @param tx vo
     */
    public  static  void setTxData(String address1, String address2, String address3, Transaction tx, TransactionMapper transactionMapper, AccountMapper addressMapper,
    		UtgStorageMinerMapper UtgstorageMinerMapper, NodeExitMapper nodeExitMapper, StorageSpaceMapper storageSpaceMapper,GlobalConfigMapper globalConfigMapper,List<Log> logs){
        if(tx.getStatus()!=1|| tx.getUfoprefix()==null||tx.getInput()==null)
                return;
        try{
        if(tx.getUfoprefix().equalsIgnoreCase(UfoPrefixEnum.UTG.getCode())){
                        utg(tx,transactionMapper,addressMapper,UtgstorageMinerMapper,nodeExitMapper,storageSpaceMapper,logs);
        }else if(tx.getUfoprefix().equalsIgnoreCase(UfoPrefixEnum.SSC.getCode())){
                        ssc(address1,address2,address3,tx,transactionMapper,UtgstorageMinerMapper,globalConfigMapper,logs);
        }
        }catch(Exception e){
        	tx.setStatus(0);
        	log.warn("setTxData error:",e);
        }
    }

    /**
     *
     * UTG
     * @param tx vo
     */
    public  static  void utg(Transaction tx, TransactionMapper transactionMapper, AccountMapper addressMapper,
    		UtgStorageMinerMapper UtgstorageMinerMapper, NodeExitMapper nodeExitMapper, StorageSpaceMapper storageSpaceMapper, List<Log> logs){
         if(logs==null){
            tx.setStatus(0);
            return;
         }
         List<String> topics = logs==null||logs.size()==0 ? null : logs.get(0).getTopics();
         String logData = logs==null||logs.size()==0 ? null : logs.get(0).getData();
         String data = tx.getInput();
         String ufoParameter = null;
         String srcData =null;
         if(data.startsWith(UTGOperatorEnum.Exch.getEncode())){
             BigInteger utg = null;
             BigInteger srt = null;      //srt
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(exchTopic)){
                 utg = bigNumberConvert(logData.substring(2,66));
                 srt = convertToBigDecimal("0x"+logData.substring(67,130)).toBigInteger();
             }else{
                 tx.setStatus(0);
                 return;
             }
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.Exch.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String [] split = ufoParameter.split(":");
             tx.dataBuild(addressParse(split[0]),null,
                     utg.toString(),
                     srt==null?null:srt.toString(),null,null);
         }else if(data.startsWith(UTGOperatorEnum.Bind.getEncode())){
              String type = null;
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(bindTopic)){
                 type = convertToBigDecimal(topics.get(2)).toString();
             }else{
                 tx.setStatus(0);
                 return;
             }
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.Bind.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String [] split = ufoParameter.split(":");
             String miner_addr = addressParse(split[0]);
         //    String revenue_address = tx.getFromAddr();
             String revenue_address = addressParse(logData.substring(26, 66));
             tx.dataBuild(miner_addr,revenue_address,type,null,addressParse(split[2]),addressParse(split[3]));
             //Income address binding income type 0 node reward 1 traffic mining
             if(Integer.parseInt(type)==0){
                 //Node binding
                 UtgNodeMiner node = new UtgNodeMiner();
                 node.setNode_address(miner_addr);
                 node.setRevenue_address(revenue_address);
                 node.setSync_time(new Date());
                 nodeExitMapper.updateNodeMiner(node);
             }else if(Integer.parseInt(type)==1){            
            	 if(logData!=null && logData.length()>=66){
            		 revenue_address = Constants.PRE+logData.substring(26,66);
            		 tx.dataBuild(miner_addr,revenue_address,type,null,addressParse(split[2]),addressParse(split[3]));
            	 }
                 UtgStorageMiner pre_miner = UtgstorageMinerMapper.getSingleMiner(miner_addr);
                 if(pre_miner==null){
                     UtgStorageMiner minerInsert = new UtgStorageMiner(miner_addr,revenue_address,MinerStatusEnum.toBeAddPool.getCode(),tx.getBlockNumber(),new Date());
                     UtgstorageMinerMapper.saveOrUpdata(minerInsert);
                 }else{
                     UtgStorageMiner minerUpdate = new UtgStorageMiner(miner_addr,revenue_address,pre_miner.getMiner_status(),tx.getBlockNumber(),new Date());
                     UtgstorageMinerMapper.updateStorageMiner(minerUpdate);                   	 
                 }
                 addressMapper.setAsRevenue(revenue_address,tx.getBlockNumber());
                 //Update storage space revenue address
                 StorageSpace space = storageSpaceMapper.getSpaceInfo(miner_addr);
                 if(space!=null){                	 
                	 space.setRevenueAddr(revenue_address);
                	 space.setUpdatetime(new Date());
                	 storageSpaceMapper.updateRevenueAddr(space);
                 }     			 
             }
         }else if(data.startsWith(UTGOperatorEnum.Unbind.getEncode())){
             String type = null;
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(bindTopic)){
                 type = convertToBigDecimal(topics.get(2)).toString();
             }else{
                 tx.setStatus(0);
                 return;
             }
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.Unbind.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String [] split = ufoParameter.split(":");
             String miner_addr = addressParse(split[0]);
        //     String revenue_address = tx.getFromAddr();
             tx.dataBuild(miner_addr,null,type,null,null,null);
             if(Integer.parseInt(type)==0){
                 //Node revenue address unbinding
                 UtgNodeMiner node = new UtgNodeMiner();
                 node.setNode_address(miner_addr);
                 node.setRevenue_address("");
                 node.setSync_time(new Date());
                 nodeExitMapper.updateNodeMiner(node);
             }else if(Integer.parseInt(type)==1){
                 //Unbind income address
                 UtgStorageMiner miner = new UtgStorageMiner();
                 miner.setMiner_addr(miner_addr);
                 miner.setRevenue_address("");
                 miner.setBlocknumber(tx.getBlockNumber());
                 miner.setSync_time(new Date());
                 UtgstorageMinerMapper.updateStorageMiner(miner);
                 
                 //Update storage space revenue address
                 StorageSpace space = storageSpaceMapper.getSpaceInfo(miner_addr);
                 if(space!=null){                	 
                	 space.setRevenueAddr("");
                	 space.setUpdatetime(new Date());
                	 storageSpaceMapper.updateRevenueAddr(space);
                 }
             }
         }else if(data.startsWith(UTGOperatorEnum.Rebind.getEncode())){
             String type = null;
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(bindTopic)){
                 type = convertToBigDecimal(topics.get(2)).toString();
             }else{
                 tx.setStatus(0);
                 return;
             }
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.Rebind.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String [] split = ufoParameter.split(":");
             String miner_addr = addressParse(split[0]);
             String revenue_address = addressParse(split[4]);
             tx.dataBuild(miner_addr,revenue_address ,type,null,addressParse(split[2]),addressParse(split[3]));
             if(Integer.parseInt(type)==0){
                 //Node revenue address replacement
                 UtgNodeMiner node = new UtgNodeMiner();
                 node.setNode_address(miner_addr);
                 node.setRevenue_address(revenue_address);
                 node.setSync_time(new Date());
                 nodeExitMapper.updateNodeMiner(node);
             }else if(Integer.parseInt(type)==1){
                 //Change of income address
                 UtgStorageMiner miner = new UtgStorageMiner();
                 miner.setMiner_addr(miner_addr);
                 miner.setRevenue_address(revenue_address);
                 miner.setBlocknumber(tx.getBlockNumber());
                 miner.setSync_time(new Date());
                 if(!zeroA.equalsIgnoreCase(split[3])){
                     miner.setAddpool(1);
                 }
                 UtgstorageMinerMapper.updateStorageMiner(miner);
                 addressMapper.setAsRevenue(revenue_address,tx.getBlockNumber());
               //Update storage space revenue address
                 StorageSpace space = storageSpaceMapper.getSpaceInfo(miner_addr);
                 if(space!=null){                	 
                	 space.setRevenueAddr(revenue_address);
                	 space.setUpdatetime(new Date());
                	 storageSpaceMapper.updateRevenueAddr(space);
                 }
             }
         }else if(data.startsWith(UTGOperatorEnum.CandReq.getEncode())){
        	 srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandReq.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String address = addressParse(ufoParameter.substring(0));
        //     String address = null;
             String type  = null;
             BigDecimal pledgeAmout = null;
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(candReqTopic)){
         //   	 address = addressParse(topics.get(1).substring(26));
                 type = convertToBigDecimal(topics.get(2)).toString();
          //       type = convertToBigDecimal(topics.get(2).substring(65)).toString();
                 pledgeAmout = convertToBigDecimal("0x"+logData.substring(2,66));
             }else{
                 tx.setStatus(0);
                 return;
             }
             tx.dataBuild(address,null,type,pledgeAmout.toString(),null,null);

          //   UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
          //   queryForm.setNode_address(address);
          //   UtgNodeMiner pre_node = nodeExitMapper.getNode(queryForm);
             UtgNodeMiner node = new UtgNodeMiner() ;
             node.setNode_address(address);
             node.setManage_address(tx.getFromAddr());
             node.setNode_type(NodeTypeEnum.wait.getCode()); 
             node.setBlocknumber(tx.getBlockNumber());
          //   if(pre_node!=null){            	
          //       node.setPledge_amount(add(pre_node.getPledge_amount(),pledgeAmout));
          //   }else{
                 node.setPledge_amount(pledgeAmout);
          //   }
             node.setTotal_amount(pledgeAmout);
             node.setJoin_time(tx.getTimeStamp());
             node.setSync_time(new Date());
             nodeExitMapper.saveOrUpdateNodeMiner(node);
             
             NodePledge nodePledge = new NodePledge();
             nodePledge.setNode_type("PoS");
             nodePledge.setNode_address(address);
             nodePledge.setPledge_address(tx.getFromAddr());
             nodePledge.setPledge_hash(tx.getHash());
             nodePledge.setPledge_amount(pledgeAmout);
             nodePledge.setPledge_status(1);
             nodePledge.setPledge_number(tx.getBlockNumber());
             nodePledge.setPledge_time(tx.getTimeStamp());
             nodeExitMapper.saveOrUpdateNodePledge(nodePledge);
             
             addressMapper.setAsMiner(address,tx.getBlockNumber());
         }else if(data.startsWith(UTGOperatorEnum.CandEntrust.getEncode())){
        	 if(topics==null||topics.size()<3){
        		 tx.setStatus(0);
                 return;
        	 }
        	 srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandEntrust.getEncode().length())));
        	 ufoParameter = srcData.substring(1);
        //	 String address = addressParse(ufoParameter.substring(0));
        	 BigInteger pledge_amount = new BigInteger(ufoParameter.split(":")[1],16);
        	 String node_address = addressParse(topics.get(1).substring(26));
        	 String type = bigNumberConvert(topics.get(2).substring(65)).toString();
        	 tx.dataBuild(node_address,null,type,pledge_amount.toString(),null,null); 
        	 
        	 UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
             queryForm.setNode_address(node_address);
             UtgNodeMiner nodeMiner = nodeExitMapper.getNode(queryForm);
             if(nodeMiner==null){
            	 log.warn("Node address "+node_address+" don't exist.");
            	 tx.setStatus(0);
                 return;
             }
        	 
             NodePledge nodePledge = new NodePledge();
             nodePledge.setNode_type("PoS");
             nodePledge.setNode_address(node_address);
             nodePledge.setPledge_address(tx.getFromAddr());
             nodePledge.setPledge_hash(tx.getHash());
             nodePledge.setPledge_amount(new BigDecimal(pledge_amount));
             nodePledge.setPledge_status(1);
             nodePledge.setPledge_number(tx.getBlockNumber());
             nodePledge.setPledge_time(tx.getTimeStamp());
             nodeExitMapper.saveOrUpdateNodePledge(nodePledge);
             BigDecimal total_amount = nodeMiner.getTotal_amount() ==null?nodeMiner.getPledge_amount():nodeMiner.getTotal_amount();
             total_amount = total_amount.add(new BigDecimal(pledge_amount));
             nodeMiner.setTotal_amount(total_amount);
             nodeExitMapper.saveOrUpdateNodeMiner(nodeMiner);
         }else if(data.startsWith(UTGOperatorEnum.CandETExit.getEncode())){
        	 if(topics==null||topics.size()<3){
        		 tx.setStatus(0);
                 return;
        	 }
        	 srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandETExit.getEncode().length())));
        	 ufoParameter = srcData.substring(1);
        //	 String address = addressParse(ufoParameter.substring(0));        	 
        	 String pledge_hash = Constants.PRE+ufoParameter.split(":")[1];
        	 String node_address = addressParse(topics.get(1).substring(26));
        	 String type = bigNumberConvert(topics.get(2).substring(65)).toString();             
        	 tx.dataBuild(node_address,pledge_hash,type,null,null,null);
        	 
        	 UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
             queryForm.setNode_address(node_address);
             UtgNodeMiner nodeMiner = nodeExitMapper.getNode(queryForm);
             if(nodeMiner==null){
            	 log.warn("Node address "+node_address+" don't exist.");
            	 tx.setStatus(0);
                 return;
             }
             
             NodePledge nodePledge = nodeExitMapper.getNodePledgeByHash(pledge_hash);
             if(nodePledge==null){
            	 log.warn("Node pledge hash "+pledge_hash+" don't exist.");
            	 tx.setStatus(0);
                 return;
             }
             nodePledge.setPledge_status(0);
             nodePledge.setUnpledge_type(0);
             nodePledge.setUnpledge_hash(tx.getHash());
             nodePledge.setUnpledge_number(tx.getBlockNumber());
             nodePledge.setUnpledge_time(tx.getTimeStamp());             
             nodeExitMapper.saveOrUpdateNodePledge(nodePledge);
             
             BigDecimal total_amount = nodeMiner.getTotal_amount() ==null?nodeMiner.getPledge_amount():nodeMiner.getTotal_amount();
             total_amount = total_amount.subtract(nodePledge.getPledge_amount());
             nodeMiner.setTotal_amount(total_amount);             
             nodeExitMapper.saveOrUpdateNodeMiner(nodeMiner);
         }else if(data.startsWith(UTGOperatorEnum.CandChaRate.getEncode())){
        	 if(topics==null||topics.size()<3){
        		 tx.setStatus(0);
                 return;
        	 }
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandChaRate.getEncode().length())));
             ufoParameter = srcData.substring(1);             
          //   String address = addressParse(ufoParameter.substring(0));
             String node_address = addressParse(topics.get(1).substring(26));
			 Integer rate = new BigInteger(topics.get(2).substring(2), 16).intValue();
             tx.dataBuild(node_address,null,rate.toString(),null,null,null);
             
             UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
             queryForm.setNode_address(node_address);
             UtgNodeMiner nodeMiner = nodeExitMapper.getNode(queryForm);
             if(nodeMiner==null){
            	 log.warn("Node address "+node_address+" don't exist.");
            	 tx.setStatus(0);
                 return;
             }
             
             nodeMiner.setRate(rate);             
             nodeExitMapper.saveOrUpdateNodeMiner(nodeMiner);
         }else if(data.startsWith(UTGOperatorEnum.CandExit.getEncode())){
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandExit.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String address = addressParse(ufoParameter.substring(0));
             String type  = null;
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(pledgeExitTopic)){
                 type = convertToBigDecimal(topics.get(2)).toString();
             }else{
                 tx.setStatus(0);
                 return;
             }
             tx.dataBuild(address,null,type,null,null,null);
             
         }else if(data.startsWith(UTGOperatorEnum.CandPnsh.getEncode())){
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.CandPnsh.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String address = addressParse(ufoParameter.substring(0));
             Integer mbpoint = 0;
             BigInteger fee = null;
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(candPnshTopic)){
                 mbpoint = convertToBigDecimal("0x"+logData.substring(2,66)).intValue();
                 fee = convertToBigDecimal("0x"+logData.substring(67,130)).toBigInteger();
             }else{
                 tx.setStatus(0);
                 return;
             }
             tx.dataBuild(address,null,mbpoint==null?null:mbpoint.toString(),
                     fee==null?null:fee.toString(),null,null);
             //Block out penalties to make up
             UtgNodeMinerQueryForm queryForm = new  UtgNodeMinerQueryForm();
             queryForm.setNode_address(address);
             UtgNodeMiner pre_node = nodeExitMapper.getNode(queryForm);
             if(pre_node!=null){
                 UtgNodeMiner node = new UtgNodeMiner();
                 node.setNode_address(address);
                 node.setFractions(0);
                 node.setPledge_amount(add(pre_node.getPledge_amount(),new BigDecimal(fee)));
                 node.setSync_time(new Date());
                 nodeExitMapper.updateNodeMiner(node);
             }             
         //    Addresses addressZero = new Addresses(Constants.addressZero,tx.getBlockNumber());             
         //    BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();             
         //    addressZero.setBalance(new BigDecimal(balance));
         //    addressMapper.saveOrUpdata(addressZero);             
         }else if(data.startsWith(UTGOperatorEnum.FlwReq.getEncode())){
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.FlwReq.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String [] split = ufoParameter.split(":");
             String miner_address = addressParse(split[0]);
             String line_type = bigNumberConvert(split[1]).toString();
             String bandwidth = bigNumberConvert(split[2]).toString();
             String type  = null;
             BigDecimal pledgeAmout = null;
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(flwReqTopic)){
                 type = convertToBigDecimal(topics.get(2)).toString();
                 pledgeAmout =  convertToBigDecimal("0x"+logs.get(1).getData().substring(67,130));
             }else{
                 tx.setStatus(0);
                 return;
             }
             tx.dataBuild(miner_address, null, bandwidth,pledgeAmout==null?null:pledgeAmout.toString(),
                     line_type,type);
             //Data mining pledge
             UtgStorageMiner pre_miner = UtgstorageMinerMapper.getSingleMiner(miner_address);
             if(pre_miner!=null){
                 UtgStorageMiner miner = new UtgStorageMiner();
                 miner.setMiner_addr(miner_address);
                 miner.setSync_time(new Date());
                 miner.setMiner_status(MinerStatusEnum.mining.getCode());
                 miner.setLine_type(line_type);
                 miner.setBandwidth(new BigDecimal(bandwidth));
                 miner.setPledge_amount(add(pre_miner.getPledge_amount(),pledgeAmout));
                 miner.setBlocknumber(tx.getBlockNumber());
                 miner.setJoin_time(tx.getTimeStamp());
                 UtgstorageMinerMapper.updateStorageMiner(miner);
             }
         }else if(data.startsWith(UTGOperatorEnum.FlwExit.getEncode())){
             srcData = new String(Numeric.hexStringToByteArray(data.substring(UTGOperatorEnum.FlwExit.getEncode().length())));
             ufoParameter = srcData.substring(1);
             String miner_address = addressParse(ufoParameter.substring(0));
             String type  = null;
             if(topics!=null&&topics.size()>0&&topics.get(0).equalsIgnoreCase(pledgeExitTopic)){
                 type = convertToBigDecimal(topics.get(2)).toString();
             }else{
                 tx.setStatus(0);
                 return;
             }
             tx.dataBuild(miner_address,null,type,null,null,null);
         }
    }


    /**
     * SSC
     * @param tx vo
     */
    public  static  void ssc(String address1, String address2, String address3, Transaction tx, TransactionMapper transactionMapper, UtgStorageMinerMapper UtgstorageMinerMapper, 
    		GlobalConfigMapper globalConfigMapper,List<Log> logs){
        String data = tx.getInput();
        String ufoParameter = null;
        String srcData = null;
        if(data.startsWith(SscOperatorEnum.ExchRate.getEncode())){
            if(!address1.equalsIgnoreCase(tx.getFromAddr())){
                tx.setStatus(0);
                return;
            }
            srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.ExchRate.getEncode().length())));
            ufoParameter = srcData.substring(1);
            GlobalConfig config = new GlobalConfig();
    		config.setType("ExchRate");
    		config.setValue(ufoParameter);
    		config.setTxhash(tx.getHash());
    		config.setBlocknumber(tx.getBlockNumber());
    		config.setUpdatetime(tx.getTimeStamp());
    		globalConfigMapper.saveOrUpdate(config);
            tx.dataBuild(ufoParameter,null,null,null,null,null);
        }else if(data.startsWith(SscOperatorEnum.Deposit.getEncode())){
            if(!address2.equalsIgnoreCase(tx.getFromAddr())){
                tx.setStatus(0);
                return;
            }
            srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.Deposit.getEncode().length())));
            ufoParameter = srcData.substring(1);
            String [] split = ufoParameter.split(":");
            String type = split[1];            
			if (!"0".equals(type)) {
				String value = bigNumberConvert(split[0]).toString();
				tx.setUfooperator("stset");
				tx.setParam1(type);
				tx.setParam2(value);
				GlobalConfig config = new GlobalConfig();
				config.setType(type);
				config.setValue(value);
				config.setTxhash(tx.getHash());
				config.setBlocknumber(tx.getBlockNumber());
				config.setUpdatetime(tx.getTimeStamp());
				globalConfigMapper.saveOrUpdate(config);
                if("9".equals(type)){
                    GlobalConfig config4 = new GlobalConfig();
                    config4.setType("4");
                    config4.setValue(value);
                    config4.setTxhash(tx.getHash());
                    config4.setBlocknumber(tx.getBlockNumber());
                    config4.setUpdatetime(tx.getTimeStamp());
                    globalConfigMapper.saveOrUpdate(config4);
                }
            }else{
            	tx.dataBuild(bigNumberConvert(split[0]).toString(),split[1],null,null,null,null);
            }
        }else if(data.startsWith(SscOperatorEnum.CndLock.getEncode())){
            if(!address2.equalsIgnoreCase(tx.getFromAddr())){
                tx.setStatus(0);
                return;
            }
            srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.CndLock.getEncode().length())));
            ufoParameter = srcData.substring(1);
            String [] split = ufoParameter.split(":");
            tx.dataBuild(bigNumberConvert(split[0]).toString(),
                    bigNumberConvert(split[1]).toString(),
                    bigNumberConvert(split[2]).toString(),null,null,null);
        }else if(data.startsWith(SscOperatorEnum.FlwLock.getEncode())){	
            if(!address2.equalsIgnoreCase(tx.getFromAddr())){
                tx.setStatus(0);
                return;
            }
            srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.FlwLock.getEncode().length())));
            ufoParameter = srcData.substring(1);
            String [] split = ufoParameter.split(":");
            tx.dataBuild(bigNumberConvert(split[0]).toString(),
                    bigNumberConvert(split[1]).toString(),
                    bigNumberConvert(split[2]).toString(),null,null,null);
        }else if(data.startsWith(SscOperatorEnum.RwdLock.getEncode())){
            if(!address2.equalsIgnoreCase(tx.getFromAddr())){
                tx.setStatus(0);
                return;
            }
            srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.RwdLock.getEncode().length())));
            ufoParameter = srcData.substring(1);
            String [] split = ufoParameter.split(":");
            tx.dataBuild(bigNumberConvert(split[0]).toString(),
                    bigNumberConvert(split[1]).toString(),
                    bigNumberConvert(split[2]).toString(),null,null,null);
        }else if(data.startsWith(SscOperatorEnum.WdthPnsh.getEncode())){
            if(!address3.equalsIgnoreCase(tx.getFromAddr())){
                tx.setStatus(0);
                return;
            }
            srcData = new String(Numeric.hexStringToByteArray(data.substring(SscOperatorEnum.WdthPnsh.getEncode().length())));
            ufoParameter = srcData.substring(1);
            String [] split = ufoParameter.split(":");
            tx.dataBuild(addressParse(split[0]),null,bigNumberConvert(split[1]).toString(),null,null,null);
        }
    }

    /**
     * 40-byte address plus prefix
     */
    public static String addressParse(String address){
              return Constants.PRE + address;
    }

    /**
     * 16 to 10
     * @param num
     * @return
     */
    public static BigInteger bigNumberConvert(String num){
        return  new BigInteger(num,16);
    }


    private static BigDecimal convertToBigDecimal(String value) {
        if (null != value)
            return new BigDecimal(Numeric.decodeQuantity(value));
        return null;
    }

    public static BigDecimal add(BigDecimal a,BigDecimal b ){
        if(a == null)
            return b;
        if(b == null)
            return a;
        return a.add(b);
    }

    public static void main(String[] args) {
//        long secords=31536000;
//        long blockintal=10;
//        long blockreward=105000000;
//        BigDecimal blocknum=new BigDecimal(secords/blockintal);
//        for(int i=1;i<5;i++) {
//            BigDecimal  tbn=new BigDecimal(blockreward*(1-Math.pow(0.5,i/6.0)));
//            BigDecimal  tbn_1=new BigDecimal(blockreward*(1-Math.pow(0.5,(i-1)/6.0)));
//            BigDecimal  b=tbn.subtract(tbn_1).divide(blocknum,20,BigDecimal.ROUND_HALF_UP);
//
//        }
        double x = 210000d/420000;
        for(int i=1;i<10;i++) {
            BigDecimal b =      new BigDecimal(x* Math.pow(1-0.04,i/420000)) ;
            System.out.println(b);
        }
    }


}
