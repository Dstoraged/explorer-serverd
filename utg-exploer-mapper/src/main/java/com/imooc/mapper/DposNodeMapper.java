package com.imooc.mapper;

import com.imooc.pojo.DposNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DposNodeMapper {
    void saveNodeDpos(@Param("item")DposNode dposNode);

    List<DposNode> selectVoterNodeInfo(@Param("round")Integer round, @Param("pageCurrent")Long pageCurrent,@Param("pageSize") Long pageSize);

    long getTotalCountForRound(@Param("round")Integer round);

    List<DposNode> getHistoryNodeInfo(@Param("round")Integer rount,@Param("pageCurrent") Long pageCurrent,@Param("pageSize") Long pageSize,@Param("address") String address);

    long getHistoryNodeTotal(@Param("round")Integer rount,@Param("address") String address);

    List<DposNode> selectVoterNodeByBlocknumber(@Param("blocknumber")Long blocknumber);
}
