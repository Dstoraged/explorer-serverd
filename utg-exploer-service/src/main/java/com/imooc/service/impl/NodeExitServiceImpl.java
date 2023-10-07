package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.enums.NodeTypeEnum;
import com.imooc.mapper.GlobalConfigMapper;
import com.imooc.mapper.NodeExitMapper;
import com.imooc.mapper.StoragePoolMapper;
import com.imooc.mapper.StorageSpaceMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.mapper.TransferMinerMapper;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.GlobalConfig;
import com.imooc.pojo.NodeExit;
import com.imooc.pojo.NodePledge;
import com.imooc.pojo.Punished;
import com.imooc.pojo.StoragePool;
import com.imooc.pojo.StorageSpace;
import com.imooc.pojo.vo.TransferMinerVo;
import com.imooc.service.NodeExitService;
import com.imooc.utils.Constants;
import com.imooc.utils.ResultMap;
import com.imooc.utils.SscOperatorEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NodeExitServiceImpl implements NodeExitService {
    @Autowired
    private NodeExitMapper nodeExitMapper;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private TransferMinerMapper transferMinerMapper;
    @Autowired
    private StoragePoolMapper  poolMapper;
    @Autowired
    private StorageSpaceMapper spaceMapper;
    @Autowired
	private GlobalConfigMapper globalConfigMapper;
    @Override
    public ResultMap<NodeExit> getNodeExit(BlockQueryForm blockQueryForm) {
        Page page = blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        List<Punished> listInfo =nodeExitMapper.getPageList(blockQueryForm);
        long total=nodeExitMapper.getTotal();
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap<Page<UtgNodeMiner>> getNodeList(UtgNodeMinerQueryForm UtgNodeMinerVo) {
        Page page = UtgNodeMinerVo.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        UtgNodeMinerVo.setCurrent(pageCurrent);
        UtgNodeMinerVo.setSize(pageSize);
        List<UtgNodeMiner> listInfo =nodeExitMapper.getNodePageList(UtgNodeMinerVo);
        long total=nodeExitMapper.getNodeTotal(UtgNodeMinerVo);
        if(total>0){
            if(UtgNodeMinerVo.getNode_type()!=null && UtgNodeMinerVo.getNode_type()==3){  //Exit node
                for (UtgNodeMiner node: listInfo) {
                    node.setLeftamount(nodeExitMapper.getLeftamount(node.getNode_address(),3));
                }
            }
            page.setRecords(listInfo);
            page.setTotal(total);
            return ResultMap.getSuccessfulResult(page);
        }else{
            return ResultMap.getSuccessfulResult(null);
        }
    }    
    
    @Override
    public ResultMap getNode(UtgNodeMinerQueryForm UtgNodeMinerVo) {
        UtgNodeMiner  node = nodeExitMapper.getNode(UtgNodeMinerVo);
        if(node==null){
            node = new UtgNodeMiner();
        }
        long manage_pledge_count=nodeExitMapper.getNodeManagePledgeCount(UtgNodeMinerVo.getNode_address());
        node.setManage_pledge_count(manage_pledge_count);
        BlockQueryForm blockQueryForm = new BlockQueryForm();
        blockQueryForm.setAddress(UtgNodeMinerVo.getNode_address());
        blockQueryForm.setType(1);
        TransferMinerVo tm = transferMinerMapper.getMinerSum(blockQueryForm);
        if(tm!=null){
            node.setLockamount(tm.getTotalAmount());
            node.setReleaseamount(tm.getReleaseamount());
        }
        blockQueryForm.setType(3);
        TransferMinerVo tm2 = transferMinerMapper.getMinerSum(blockQueryForm);
        if(tm2!=null){
            node.setExitlockamount(tm2.getTotalAmount());
            node.setExitreleaseamount(tm2.getReleaseamount());
        }
        
        blockQueryForm.setType(1);
        blockQueryForm.setAddress(null);
        blockQueryForm.setPledgeaddress(UtgNodeMinerVo.getNode_address());        
        TransferMinerVo pledge = transferMinerMapper.getMinerSum(blockQueryForm);
        if(pledge!=null){
            node.setPledgelockamount(pledge.getTotalAmount());
            node.setPledgereleaseamount(pledge.getReleaseamount());
        }
        
        //Deprecated
        String version = UtgNodeMinerVo.getVersion();
		if ("2".equals(version)) {
			blockQueryForm.setType(11);
			TransferMinerVo tm11 = transferMinerMapper.getMinerSum(blockQueryForm);
			if (tm11 != null) {
				BigDecimal lockamount = node.getLockamount();
				if (tm11.getTotalAmount() != null) {
					lockamount = lockamount == null ? tm11.getTotalAmount() : lockamount.add(tm11.getTotalAmount());
					node.setLockamount(lockamount);
				}
				BigDecimal releaseamount = node.getReleaseamount();
				if (tm11.getReleaseamount() != null) {
					releaseamount = releaseamount == null ? tm11.getReleaseamount() : releaseamount.add(tm11.getReleaseamount());
					node.setReleaseamount(releaseamount);
				}
			}

			blockQueryForm.setType(14);
			TransferMinerVo tm14 = transferMinerMapper.getMinerSum(blockQueryForm);
			if (tm14 != null) {
				BigDecimal lockamount = node.getExitlockamount();
				if (tm14.getTotalAmount() != null) {
					lockamount = lockamount == null ? tm14.getTotalAmount() : lockamount.add(tm14.getTotalAmount());
					node.setExitlockamount(lockamount);
				}
				BigDecimal releaseamount = node.getExitreleaseamount();
				if (tm11.getReleaseamount() != null) {
					releaseamount = releaseamount == null ? tm14.getReleaseamount() : releaseamount.add(tm14.getReleaseamount());
					node.setExitreleaseamount(releaseamount);
				}
			}
		}
		//Deprecated
        return  ResultMap.getSuccessfulResult(node);
    }
    public  ResultMap getcadnodeByRev( UtgNodeMinerQueryForm UtgNodeMinerVo){
        return  ResultMap.getSuccessfulResult(nodeExitMapper.getNodeByRev(UtgNodeMinerVo));
    }
    public   ResultMap getNodeExistRealse( UtgNodeMinerQueryForm UtgNodeMinerVo){
        return  ResultMap.getSuccessfulResult(nodeExitMapper.getNodeExistRealse(UtgNodeMinerVo.getAddress()));
    }

    @Override
    public ResultMap getNodePledgeAmount(UtgNodeMinerQueryForm UtgNodeMinerVo) throws  Exception {
        List<Transaction> deposit = transactionMapper.getTransactionByTxType(SscOperatorEnum.Deposit.getCode());
        String pledge = null;
        if(deposit.size()>0){
            pledge =  deposit.get(0).getParam1();
        }else{
            throw  new Exception("no Deposit config set");
        }
        Map<String ,Object> m = new HashMap<>();
        m.put("TOTALMBPOINT",Constants.TOTALMBPOINT);
        m.put("NODE_PLEDGE_AMOUNT",pledge);
        return ResultMap.getSuccessfulResult(m);
//        if(StringUtils.isEmpty(UtgNodeMinerVo.getNode_address())){
//            return ResultMap.getSuccessfulResult(m);
//        }
//        UtgNodeMiner node = nodeExitMapper.getNode(UtgNodeMinerVo);
//        if(node!=null&&node.getNode_type()!=NodeTypeEnum.exit.getCode()){
//            m.put("TOTALMBPOINT",Constants.TOTALMBPOINT);
//            m.put("NODE_PLEDGE_AMOUNT",0);
//            return ResultMap.getSuccessfulResult(m);
//        }else {
//            return ResultMap.getSuccessfulResult(m);
//        }
    }


    @Override
    public ResultMap<Page<UtgNodeMiner>> getNodeRewardList(UtgNodeMinerQueryForm UtgNodeMinerVo) {
        Page page = UtgNodeMinerVo.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        UtgNodeMinerVo.setCurrent(pageCurrent);
        UtgNodeMinerVo.setSize(pageSize);
        
        List<UtgNodeMiner> listInfo =nodeExitMapper.getNodeRewardList(UtgNodeMinerVo);
        long total=nodeExitMapper.getNodeTotal(UtgNodeMinerVo);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);        
    }
    
    
    @Override
    public ResultMap<Page<UtgNodeMiner>> getNodePledgeList(UtgNodeMinerQueryForm UtgNodeMinerVo) {
        Page page = UtgNodeMinerVo.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        UtgNodeMinerVo.setCurrent(pageCurrent);
        UtgNodeMinerVo.setSize(pageSize);
        List<NodePledge> list = nodeExitMapper.getNodePledgePageList(UtgNodeMinerVo);
        long total=nodeExitMapper.getNodePledgeCount(UtgNodeMinerVo);
        UtgNodeMinerVo.setPledge_status(1);
        BigDecimal pledgeAmount = nodeExitMapper.getNodePledgeStat(UtgNodeMinerVo);
        List<GlobalConfig>   globalList=globalConfigMapper.getList();
        long daynumber=UtgNodeMinerVo.getDayNumber()==null?8640:UtgNodeMinerVo.getDayNumber();
        long posentrustday=30*daynumber;
        long snentrustday=7*daynumber;
        long spentrustday=7*daynumber;
        if(globalList!=null&&globalList.size()>0) {
        	for(GlobalConfig config:globalList) {
        		if("posentrustday".equals(config.getType())) {
        			posentrustday=	Long.valueOf(config.getValue())*daynumber;
        		}
        		if("snentrustday".equals(config.getType())) {
        			snentrustday=	Long.valueOf(config.getValue())*daynumber;
        		}
        		if("spentrustday".equals(config.getType())) {
        			spentrustday=	Long.valueOf(config.getValue())*daynumber;
        		}
        	}
        }
//       List<NodePledge>  stList= nodeExitMapper.getNodePledgeTotalSt(new NodePledge());
//       List<String> tranferList=new ArrayList<String>();
//       if(UtgNodeMinerVo.getBlockNumber()!=null&& UtgNodeMinerVo.getBlockNumber()>0&&stList!=null) {
//    	   for(NodePledge  pledge:stList) {
//        	   long endNumber=0;
//    			if("PoS".equals(pledge.getNode_type())) {
//    				endNumber=UtgNodeMinerVo.getBlockNumber()-posentrustday;
//    			}else if("SP".equals(pledge.getNode_type())) {
//    				endNumber=UtgNodeMinerVo.getBlockNumber()-spentrustday;
//    			}else if("SN".equals(pledge.getNode_type())) {
//    				endNumber=UtgNodeMinerVo.getBlockNumber()-snentrustday;
//    			}
//    			if(pledge.getPledge_number()<endNumber) {
//    				tranferList.add(pledge.getPledge_address());
//    			}
//           }
//       }
       
		for (NodePledge pledge : list) {
			if(pledge.getPledge_status()==1){
				BigDecimal ratio = pledge.getPledge_amount().divide(pledgeAmount, 4, BigDecimal.ROUND_HALF_UP);
				pledge.setRatio(ratio);
			}
			pledge.setTransfer(false);
			pledge.setEntrustExit(false);
			if(pledge.getPledge_status()!=null && pledge.getPledge_status()==1) {
				
				long endNumber=0;
				if(UtgNodeMinerVo.getBlockNumber()!=null&&UtgNodeMinerVo.getBlockNumber()>0) {
					if("PoS".equals(pledge.getNode_type())) {
						endNumber=UtgNodeMinerVo.getBlockNumber()-posentrustday;
					}else if("SP".equals(pledge.getNode_type())) {
						endNumber=UtgNodeMinerVo.getBlockNumber()-spentrustday;
					}else if("SN".equals(pledge.getNode_type())) {
						endNumber=UtgNodeMinerVo.getBlockNumber()-snentrustday;
					}
				}
				
				if(pledge.getPledge_number()<endNumber) {
					pledge.setEntrustExit(true);
					pledge.setTransfer(true);
				}
			}
			
		}
        page.setRecords(list);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);        
    }
    
    
    @Override
    public ResultMap<Page<NodePledge>> getPledgeRewardList(UtgNodeMinerQueryForm UtgNodeMinerVo) {
        Page page = UtgNodeMinerVo.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        UtgNodeMinerVo.setCurrent(pageCurrent);
        UtgNodeMinerVo.setSize(pageSize);
        if(UtgNodeMinerVo.getType()==null) {
        	UtgNodeMinerVo.setType(1);
        }
        if(UtgNodeMinerVo.getType()==23&&UtgNodeMinerVo.getNode_address()!=null&&UtgNodeMinerVo.getNode_address().length()>30) {
        	UtgNodeMinerVo.setNode_address("ux"+UtgNodeMinerVo.getNode_address().substring(26));
        }
        List<NodePledge> listInfo =nodeExitMapper.getPledgeRewardList(UtgNodeMinerVo);
        long total=nodeExitMapper.getPledgeRewardCount(UtgNodeMinerVo);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);        
    }

	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<NodePledge> getNodePledgeByPledgeAddr(NodePledge info) {
		NodePledge  dbinfo=nodeExitMapper.getNodePledgeByPledgeAddr(info);
		if (dbinfo==null) {
			 return ResultMap.getFailureResult("not find pledge by pledge_Address");  
		}
		if("PoS".equals(dbinfo.getNode_type())) {
			UtgNodeMinerQueryForm posParam=new UtgNodeMinerQueryForm();
			posParam.setNode_address(dbinfo.getNode_address());
		  UtgNodeMiner  miner=	nodeExitMapper.getNode(posParam);
		  if(miner!=null) {
			  dbinfo.setEntrustRate(miner.getRate().doubleValue());
		  }
		}
		if("SP".equals(dbinfo.getNode_type())) {
		  StoragePool  pool=	poolMapper.getPoolInfo(dbinfo.getSp_hash());
		  if(pool!=null) {
			  dbinfo.setEntrustRate(pool.getEntrustRate().doubleValue());
		  }
		}
		if("SN".equals(dbinfo.getNode_type())) {
		  StorageSpace  space=	spaceMapper.getSpaceInfo(dbinfo.getNode_address());
		  if(space!=null) {
			  dbinfo.setEntrustRate(space.getEntrustRate().doubleValue());
		  }
		}
		return ResultMap.getSuccessfulResult(dbinfo);
	}
	@Override
    public ResultMap<Map<String,Object>> isPledgeTransfer(NodePledge pledge) {
		Map<String,Object> resultMap=new HashMap<>();
		BigInteger  count= nodeExitMapper.isPledgeTransfer(pledge);
		if(count!=null&&count.compareTo(BigInteger.ZERO)>0) {
			resultMap.put("isTransfer", false);
		}else {
			resultMap.put("isTransfer", true);
		}
		return  ResultMap.getSuccessfulResult(resultMap);
	}
	@Override
    public ResultMap<Map<String,Object>> isEntrustPledge(NodePledge pledge) {
		Map<String,Object>  result=new HashMap<>();
		result.put("isPledge", true);
		NodePledge param=new NodePledge();
		param.setPledge_address(pledge.getPledge_address());
		 List<NodePledge> list=nodeExitMapper.getNodePledgeTotalSt(param);
		 String node_type="";
		 if(list!=null&&list.size()>0) {
			 for(NodePledge dbpledge : list) {
				 if(!dbpledge.getNode_address().equals(pledge.getNode_address())) {
					 result.put("isPledge", false);
					 break;
				 }else {
					 node_type=dbpledge.getNode_type();
				 }
			 }
		 }
		 String managerAddr=null;
		 if("PoS".equals(node_type)) {
			 UtgNodeMinerQueryForm param1=new  UtgNodeMinerQueryForm();
			 param1.setNode_address(pledge.getNode_address());
			UtgNodeMiner  miner= nodeExitMapper.getNode(param1);
			if(miner!=null ) {
				managerAddr=miner.getManage_address();
			}
		 }else if("SP".equals(node_type)) {
			StoragePool pool= poolMapper.getPoolInfo(pledge.getNode_address());
			if(pool!=null) {
				managerAddr=pool.getManagerAddr();
			}
		 } else if("SN".equals(node_type)){
			StorageSpace space= spaceMapper.getSpaceInfo(pledge.getNode_address());
			if(space!=null) {
				managerAddr=space.getManagerAddr();
			}
		 }
		 if(managerAddr!=null && managerAddr.equals(pledge.getPledge_address())) {
			 result.put("isPledge", false);
		 }
		return  ResultMap.getSuccessfulResult(result);
	}
}
