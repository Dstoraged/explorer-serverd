package com.imooc.mapper;

import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.UtgNodeMinerQueryForm;
import com.imooc.pojo.UtgNodeMiner;
import com.imooc.pojo.NodeExit;
import com.imooc.pojo.NodePledge;
import com.imooc.pojo.Punished;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface NodeExitMapper extends MyMapper<NodeExit> {

    List<Punished> getPageList(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

    long getTotal();

    List<UtgNodeMiner> getNodePageList(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);

    long getNodeTotal(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);

    UtgNodeMiner getNode(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);

    long getNodeByRev(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);

    long getNodeExistRealse(@Param("address") String address);

    void saveOrUpdateNodeMiner(@Param("UtgNodeMiner") UtgNodeMiner UtgNodeMiner);

    void updateNodeMiner(@Param("UtgNodeMiner") UtgNodeMiner UtgNodeMiner);

    void updateNodeStatus(@Param("UtgNodeMiner") UtgNodeMiner UtgNodeMiner);
    
    List<UtgNodeMiner> getNodeListNotExit();

    void updateNodeBatch(@Param("list") List<String> list);

    BigDecimal getLeftamount(@Param("address") String  address, @Param("type")int type);
    
    List<UtgNodeMiner> getNodeRewardList(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);    
    
//    UtgNodeMiner getNodeByAddress(@Param("address") String node_address);
    
    
    long getNodeManagePledgeCount(@Param("node_address") String node_address);
    List<NodePledge> getNodePledgeListCache(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);
    
    List<NodePledge> getNodePledgeList(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);
    
    List<NodePledge> getNodePledgePageList(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);
    
    long getNodePledgeCount(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);
    void  updateNodePledgeTransfer(NodePledge pledge);
    void  updateNodePledgeExit(NodePledge pledge);
    void updateNodePledgeBySpExit(NodePledge pledge);
    void updateNodePledgeBySpace(NodePledge pledge);
    NodePledge getNodePledgeByPledgeAddr(NodePledge pledge);
    BigInteger isPledgeTransfer(NodePledge pledge);
    BigDecimal getNodePledgeStat(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);
    
    BigDecimal getNodePledgeBurnedAmount();
    
    NodePledge getNodePledgeByHash(@Param("pledge_hash") String pledge_hash);
    
    void saveOrUpdateNodePledge(@Param("nodePledge") NodePledge nodePledge);
    
    

    List<NodePledge> getPledgeRewardList(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);
    
    long getPledgeRewardCount(@Param("UtgNodeMinerQueryForm") UtgNodeMinerQueryForm UtgNodeMinerQueryForm);
    List<NodePledge>  getNodePledgeTotalSt(NodePledge nodePledge);
}
