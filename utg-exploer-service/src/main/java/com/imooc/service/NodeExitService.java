package com.imooc.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.NodeExit;
import com.imooc.pojo.NodePledge;
import com.imooc.utils.ResultMap;

public interface NodeExitService {
    ResultMap<NodeExit> getNodeExit(BlockQueryForm blockQueryForm);

    ResultMap<Page<UtgNodeMiner>> getNodeList(UtgNodeMinerQueryForm UtgNodeMinerVo);

    ResultMap getNode( UtgNodeMinerQueryForm UtgNodeMinerVo);    	
   
    ResultMap getcadnodeByRev( UtgNodeMinerQueryForm UtgNodeMinerVo);

    ResultMap getNodeExistRealse( UtgNodeMinerQueryForm UtgNodeMinerVo);

    ResultMap getNodePledgeAmount( UtgNodeMinerQueryForm UtgNodeMinerVo) throws  Exception;
    
    ResultMap getNodeRewardList(UtgNodeMinerQueryForm UtgNodeMinerVo);

	ResultMap<Page<UtgNodeMiner>> getNodePledgeList(UtgNodeMinerQueryForm UtgNodeMinerVo);

	ResultMap<Page<NodePledge>> getPledgeRewardList(UtgNodeMinerQueryForm UtgNodeMinerVo);
	ResultMap<NodePledge> getNodePledgeByPledgeAddr(NodePledge info);
	ResultMap<Map<String,Object>> isPledgeTransfer(NodePledge pledge);
    ResultMap<Map<String,Object>> isEntrustPledge(NodePledge pledge);
}
