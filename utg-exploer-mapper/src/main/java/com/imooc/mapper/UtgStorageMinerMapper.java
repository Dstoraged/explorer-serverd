package com.imooc.mapper;

import com.imooc.pojo.Form.UtgStorageMinerQueryForm;
import com.imooc.pojo.UtgBandwidthConfig;
import com.imooc.pojo.UtgCltStoragedata;
import com.imooc.pojo.UtgStorageMiner;
import com.imooc.pojo.UtgNetStatics;
import com.imooc.pojo.vo.*;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface UtgStorageMinerMapper extends MyMapper<UtgStorageMiner> {
    List<UtgStorageMiner> getPageList(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    long getTotal(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    UtgStorageMiner getUtgStorageMinerDetail(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    long getMinerDayStatisCount(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    List<UtgStorageMinerDayVo> getMinerDayStatislist(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    long getNodeCount(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    UtgStorageMiner getStatisByReAddress(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    List<UtgCltStoragedata> getUtgCltPageList(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    long getUtgCltTotal(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    void saveOrUpdata(@Param("UtgStorageMiner") UtgStorageMiner UtgStorageMiner);

    void updateStorageMiner(@Param("UtgStorageMiner") UtgStorageMiner UtgStorageMiner);

    UtgStorageMiner getSingleMiner(@Param("miner_addr") String miner_addr);

    List<UtgBandwidthConfig> getBandwidthConfig();
    //Old
    UtgStorageMiner getMinerSum1();
    //New
    UtgStorageMiner getMinerSum();

    AuthCfg getAuthCfgByAppid(@Param("appid") String appid);

    List<Area> getArea();

    List<OperatorConfig> getOperatorConfig();

    void saveOrUpdataFlwData(@Param("UtgCltStoragedata") UtgCltStoragedata UtgCltStoragedata);

    void saveOrUpdataFlwDataDay(@Param("UtgCltStoragedata") UtgCltStoragedata UtgCltStoragedata);

    void batchSaveStorageDataDay(@Param("list")List<UtgCltStoragedata> list);

    void insertUtgNetStatics(@Param("nns")UtgNetStatics nns);

    UtgNetStatics queryUtgNetStaticsByCtime(@Param("ctime") String ctime);

    UtgCltStoragedata queryUtgCltStoragedataDayByTime(@Param("ctime") String ctime);

    UtgCltStoragedata queryUtgCltStoragedataDayByAddress(@Param("address") String address);

    List<UtgCltStoragedata> queryUtgCltStoragedataDayGroupBYAddress();

    List<UtgStorageMiner> getAllMinerAddress();

    List<UtgNetStatics> queryUtgNetStaticsBetwentime(@Param("startTime") String startTime,@Param("endTime") String endTime);

    long getNetServiceRankTotal(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    List<NetRankVo>  getNetServiceRankList(@Param("UtgStorageMinerQueryForm") UtgStorageMinerQueryForm UtgStorageMinerQueryForm);

    void updateStorageMinerBatch(@Param("list")List<UtgStorageMiner> list);

    void batchSaveFlwDataDay(@Param("list")List<UtgCltStoragedata> list);

    void batchUpdateFlwDataDay(@Param("list")List<UtgCltStoragedata> list);

    List<UtgCltStoragedata> getFlwDataDayListByReportTime(@Param("ctime") String ctime);

    BigDecimal getAllBlockReward();

    BigDecimal getAllBandWidthReward();
}
