package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.DposNode;
import com.imooc.pojo.DposVotes;
import com.imooc.pojo.DposVotesWallet;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.DposVoterQueryForm;
import com.imooc.pojo.vo.DposVoterVo;
import com.imooc.utils.ResultMap;

import java.io.IOException;
import java.math.BigDecimal;

public interface DposVoterService {
    ResultMap<Page<DposVotes>> getDposVoterInfo(BlockQueryForm blockQueryForm)throws IOException;

    ResultMap<Page<DposVoterVo>> getVoterListInfo(BlockQueryForm blockQueryForm) throws IOException;

    ResultMap<Page<DposNode>> getDposNodeInfo(BlockQueryForm blockQueryForm)throws IOException;

    ResultMap getDposVoterTotalForRound() throws IOException;

    ResultMap getDposVoterAddressTotal(BlockQueryForm blockQueryForm)throws IOException;

    ResultMap<Page<DposVotes>> getDposNodeForAddress(BlockQueryForm blockQueryForm)throws IOException;

    ResultMap<Page<DposNode>> getDposNodeInfoWallet(BlockQueryForm blockQueryForm)throws IOException;

    ResultMap<DposVoterVo> getVoterListInfoForRound(BlockQueryForm blockQueryForm)throws Exception;

    ResultMap<Page<DposVotes>> getDposVoterForCandidate(BlockQueryForm blockQueryForm)throws Exception;

    ResultMap<Page<DposVotes>> getDposByRoundAndCandidateForAddress(BlockQueryForm blockQueryForm)throws Exception;

    ResultMap<Page<DposVotesWallet>> getDposVoterWallet(BlockQueryForm blockQueryForm)throws Exception;

    ResultMap<Page<DposVotesWallet>> getVotersInfoPageList(DposVoterQueryForm dposVoterQueryForm);
}
