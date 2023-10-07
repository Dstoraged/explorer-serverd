package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Blocks;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.DposVoterQueryForm;
import com.imooc.pojo.vo.BlockAndMinerVo;
import com.imooc.pojo.vo.BlocksVo;
import com.imooc.utils.ResultMap;

public interface BlockService {
    void save(Blocks block);

    ResultMap getDatas();

    ResultMap getBlockNumber();

    ResultMap<Page<BlocksVo>> pageList(BlockQueryForm blockQueryForm);

    ResultMap getBlockInfoByBlockNumber(Long blockNumber);

    ResultMap getDataForUtg();

    ResultMap getAllParamters(long types,String language);


    ResultMap<Page<Blocks>> getRewardVoterPageList(DposVoterQueryForm dposVoterQueryForm);
    ResultMap getBwPlgeRange();

    ResultMap getAddr0Data(Long blockNumber);
}
