package com.imooc.controller.job;

import com.imooc.Utils.AgentSvcTask;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.mapper.TransferMinerMapper;
import com.imooc.pojo.UtgCltStoragedata;
import com.imooc.pojo.UtgStorageMiner;
import com.imooc.pojo.UtgNetStatics;
import com.imooc.pojo.TransferMiner;
import com.imooc.utils.Constants;
import com.imooc.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhc 2021-11-18 13:56
 */
public class NetStaticsJob extends AgentSvcTask {
    private static Logger logger = LoggerFactory.getLogger(NetStaticsJob.class);
    private UtgStorageMinerMapper UtgStorageMinerMapper;
    private TransferMinerMapper transferMinerMapper;
    private Date ctime;
    private String decimals = "1000000000000000000";
    private int M_G = 1024;

    public NetStaticsJob(UtgStorageMinerMapper UtgStorageMinerMapper, TransferMinerMapper transferMinerMapper, Date ctime) {
        this.UtgStorageMinerMapper = UtgStorageMinerMapper;
        this.transferMinerMapper = transferMinerMapper;
        this.ctime = ctime;
    }

    protected  void runTask() {
        long starttime = System.currentTimeMillis();
        logger.info("NetStaticsJob start .....");
        try {
            //NetStatics
            UtgNetStatics nns = new UtgNetStatics();
            nns.setCtime(ctime);
            nns.setTotal_bw(BigDecimal.ZERO);
            UtgCltStoragedata flw = UtgStorageMinerMapper.queryUtgCltStoragedataDayByTime(DateUtil.getFormatDateTime(ctime, "yyyy-MM-dd"));
            if(flw!=null){
                nns.setTotal_utg(flw.getProfitamount());
            }
            UtgStorageMiner miner = UtgStorageMinerMapper.getMinerSum();
            if(miner!=null && miner.getBandwidth()!=null){
                nns.setTotal_bw(miner.getBandwidth());
            }
            UtgNetStatics prenns =  UtgStorageMinerMapper.queryUtgNetStaticsByCtime(DateUtil.getFormatDateTime(DateUtil.addDaysToDate(ctime,-1), "yyyy-MM-dd"));
            if(prenns!=null){
                nns.setIncre_bw(nns.getTotal_bw().subtract(prenns.getTotal_bw()));
            }else{
                nns.setIncre_bw(nns.getTotal_bw());
            }
            if(nns.getTotal_bw().compareTo(BigDecimal.ZERO)==0){
                nns.setUtg_gbrate(BigDecimal.ZERO);
            }else{
                nns.setUtg_gbrate(nns.getTotal_utg().divide(new BigDecimal(decimals),20, BigDecimal.ROUND_HALF_UP).
                        multiply(new BigDecimal(M_G)).divide(nns.getTotal_bw(),4, BigDecimal.ROUND_HALF_UP));
            }
            UtgStorageMinerMapper.insertUtgNetStatics(nns);
        }catch (Exception e) {
            logger.error("NetStaticsJob error,"+e.getMessage(),e);
        }
        minerModify();
        utgDayLockRealseModify();
        long endtime = System.currentTimeMillis();
        logger.info("NetStaticsJob end ,spends total second ="+(endtime-starttime)/1000);
    }

    private void minerModify() {
        try {
            //miner statis
            List<UtgStorageMiner> minerList = UtgStorageMinerMapper.getAllMinerAddress();
            if(minerList!=null&&minerList.size()>0){
                List<UtgCltStoragedata>  flwList = UtgStorageMinerMapper.queryUtgCltStoragedataDayGroupBYAddress();
                Map<String, UtgCltStoragedata> cltAddressMap = new HashMap<>();
                for (UtgCltStoragedata data: flwList) {
                    cltAddressMap.put(data.getEn_address().toLowerCase(),data);
                }
                List<TransferMiner> transfList = transferMinerMapper.getReleaseamountGroupByAddress();
                Map<String,TransferMiner> transfAddressMap = new HashMap<>();
                for (TransferMiner data: transfList) {
                    transfAddressMap.put(data.getAddress().toLowerCase(),data);
                }
                List<UtgStorageMiner> list = new ArrayList<>();
                for (UtgStorageMiner minner: minerList) {
                    String mAddress = minner.getMiner_addr().toLowerCase();
                    UtgCltStoragedata mflw = cltAddressMap.get(mAddress);
                    if (mflw != null) {
                        minner.setMiner_storage(mflw.getStorage_value());
                        minner.setPaysrt(mflw.getSrtnum());
                        minner.setRevenue_amount(mflw.getProfitamount());
                        cltAddressMap.remove(mAddress);
                    }
                    TransferMiner tm = transfAddressMap.get(mAddress);
                    if (tm != null) {
                        minner.setRelease_amount(tm.getReleaseamount());
                        minner.setLock_amount(tm.getTotalAmount());
                        transfAddressMap.remove(mAddress);
                    }
                    minner.setSync_time(new Date());
                    list.add(minner);
                    if(list.size()>0&&list.size()/Constants.BATCHCOUNT>=1){
                        UtgStorageMinerMapper.updateStorageMinerBatch(list);
                        list = new ArrayList<>();
                    }
                }
                if(list.size()>0){
                    UtgStorageMinerMapper.updateStorageMinerBatch(list);
                }
            }
        }catch (Exception e) {
            logger.error("MinerModifyJob error,"+e.getMessage(),e);
        }
    }

    private void utgDayLockRealseModify() {
        try {
            List<UtgCltStoragedata> utgCltList = UtgStorageMinerMapper.getFlwDataDayListByReportTime(DateUtil.getFormatDateTime(ctime, "yyyy-MM-dd"));
            if(utgCltList!=null&&utgCltList.size()>0){
                List<TransferMiner> bandwidthList = transferMinerMapper.getReleaseamountGroupByAddressAndType(9);
                Map<String,TransferMiner> bandwidthMap = new HashMap<>();
                for (TransferMiner data: bandwidthList) {
                    bandwidthMap.put(data.getAddress().toLowerCase(),data);
                }
                List<UtgCltStoragedata> list = new ArrayList<>();
                for (UtgCltStoragedata clt: utgCltList) {
                    String mAddress = clt.getEn_address().toLowerCase();
                    if(clt.getFwflag()==1){
                        TransferMiner  mbandwidth = bandwidthMap.get(mAddress);
                        if (mbandwidth != null) {
                            clt.setLockamount(mbandwidth.getTotalAmount());
                            clt.setReleaseamount(mbandwidth.getReleaseamount());
                            list.add(clt);
                        }
                    }
                    if(list.size()>0&&list.size()/Constants.BATCHCOUNT>=1){
                        UtgStorageMinerMapper.batchUpdateFlwDataDay(list);
                        list = new ArrayList<>();
                    }
                }
                if(list.size()>0){
                    UtgStorageMinerMapper.batchUpdateFlwDataDay(list);
                }
            }
        }catch (Exception e) {
            logger.error("utgDayLockRealseModify error,"+e.getMessage(),e);
        }
    }
}
