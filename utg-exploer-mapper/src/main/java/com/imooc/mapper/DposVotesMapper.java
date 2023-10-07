package com.imooc.mapper;

import com.imooc.pojo.DposNode;
import com.imooc.pojo.DposVotes;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.vo.DposVoterVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DposVotesMapper {
    void saveOrUpdate(@Param("item")DposVotes dposVotes);

    List<DposVotes> selectVoterInfo(@Param("round")Integer round,@Param("pageCurrent")Long pageCurrent, @Param("pageSize")Long pageSize);

    DposVoterVo getVoterInfo(@Param("round")int round);

   // List<DposVoterVo> getVoterPageList(@Param("pageCurrent")Long pageCurrent,@Param("pageSize") Long pageSize);

    List<DposVoterVo> getVoterPageList();

    long getVoterPageCount();

    long getTotalCountForRound(@Param("round")Integer round);

    BigDecimal getTotalStake(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

    List<DposVotes> selectVoterNodeAddress(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

    long getTotalForAddress(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    long getTotalVoterDisitint(@Param("round")int round);
}
