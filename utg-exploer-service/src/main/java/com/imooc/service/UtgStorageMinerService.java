package com.imooc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Form.UtgStorageMinerQueryForm;
import com.imooc.pojo.Form.UtgNetStaticsForm;
import com.imooc.pojo.UtgCltStoragedata;
import com.imooc.pojo.UtgStorageMiner;
import com.imooc.pojo.vo.*;
import com.imooc.utils.ResultMap;

import java.util.Map;

public interface UtgStorageMinerService {
    ResultMap<Page<UtgStorageMiner>> pageList(UtgStorageMinerQueryForm utgStorageMinerQueryForm);
    ResultMap<UtgStorageMiner> getUtgStorageMinerDetail(UtgStorageMinerQueryForm utgStorageMinerQueryForm);

    ResultMap<Page<UtgStorageMinerDayVo>>  getMinerDayStatislist(UtgStorageMinerQueryForm utgStorageMinerQueryForm);

    ResultMap<Map> outLine(UtgStorageMinerQueryForm utgStorageMinerQueryForm);

    ResultMap<Page<UtgCltStoragedata>> pageUtgCltList(UtgStorageMinerQueryForm utgStorageMinerQueryForm);


    AuthCfg getAuthCfgByAppid(String appid);

    ResultMap<Area> getArea();

    ResultMap<OperatorConfig> getOperatorConfig();

    ResultMap<Map> netBandWidth();

    ResultMap<Map> bandWidthToUtg(Long width);

    ResultMap  getMinnnerPledgeStatus(UtgStorageMinerQueryForm utgStorageMinerQueryForm);

    ResultMap<Map> getUtgNetStatics(UtgNetStaticsForm net);

    ResultMap<Map> getNetServiceRank(UtgStorageMinerQueryForm utgStorageMinerQueryForm);


}
