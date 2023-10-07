package com.imooc.mapper;


import com.imooc.pojo.DposVotesWallet;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.DposVoterQueryForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DposVoterWalletMapper {
    void saveOrUpdate(@Param("item") DposVotesWallet dposVotesWallet);

    List<DposVotesWallet> getPageList(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    long getTotal(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    List<DposVotesWallet> getVoterInfoPageList(@Param("dposVoterQueryForm")DposVoterQueryForm dposVoterQueryForm);

    long getTotalVoterInfo(@Param("dposVoterQueryForm")DposVoterQueryForm dposVoterQueryForm);
}
