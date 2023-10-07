package com.imooc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.BlockMapper;
import com.imooc.mapper.StorageStatMapper;
import com.imooc.mapper.TransferMinerMapper;
import com.imooc.mapper.UtgStorageMinerMapper;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.StorageGlobalStat;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.UtgStorageMinerQueryForm;
import com.imooc.pojo.Form.UtgNetStaticsForm;
import com.imooc.pojo.UtgBandwidthConfig;
import com.imooc.pojo.UtgCltStoragedata;
import com.imooc.pojo.UtgNetStatics;
import com.imooc.pojo.UtgStorageMiner;
import com.imooc.pojo.vo.*;
import com.imooc.service.UtgStorageMinerService;
import com.imooc.utils.Constants;
import com.imooc.utils.DateUtil;
import com.imooc.utils.OrderUtils;
import com.imooc.utils.ResultMap;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class UtgStorageMinerServiceImpl implements UtgStorageMinerService {

    @Autowired
    private UtgStorageMinerMapper UtgStorageMinerMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private BlockMapper blockMapper;
    @Autowired
    private TransferMinerMapper transferMinerMapper;
    @Autowired
    private StorageStatMapper storageStatMapper;

    public ResultMap<Page<UtgStorageMiner>> pageList(UtgStorageMinerQueryForm UtgStorageMinerQueryForm){
        Page page = UtgStorageMinerQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        UtgStorageMinerQueryForm.setCurrent(pageCurrent);
        UtgStorageMinerQueryForm.setSize(pageSize);
        String sortBy  = UtgStorageMinerQueryForm.getSortBy();
        if(sortBy!=null&&OrderUtils.UtgStorageMinerList.contains(sortBy)){
            UtgStorageMinerQueryForm.setSortSql(OrderUtils.sort(sortBy,UtgStorageMinerQueryForm.isDescending()));
        }
        List<UtgStorageMiner> listInfo =UtgStorageMinerMapper.getPageList(UtgStorageMinerQueryForm);
        long total=UtgStorageMinerMapper.getTotal(UtgStorageMinerQueryForm);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    @Override
    public ResultMap<UtgStorageMiner> getUtgStorageMinerDetail(UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        UtgStorageMiner miner = UtgStorageMinerMapper.getUtgStorageMinerDetail(UtgStorageMinerQueryForm);
        if(miner == null){
            miner = new UtgStorageMiner();
        }
        BlockQueryForm blockQueryForm = new BlockQueryForm();
        blockQueryForm.setAddress(UtgStorageMinerQueryForm.getMiner_addr());
        blockQueryForm.setType(9);
        TransferMinerVo bdvo = transferMinerMapper.getLockSummaryByRevAddress(blockQueryForm);
        if(bdvo!=null){
            miner.setBdtotalamount(bdvo.getTotalAmount());
            miner.setBdreleaseamount(bdvo.getReleaseamount());
        }
        return ResultMap.getSuccessfulResult(miner);
    }

    @Override
    public ResultMap<Page<UtgStorageMinerDayVo>> getMinerDayStatislist(UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        Page page = UtgStorageMinerQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        UtgStorageMinerQueryForm.setCurrent(pageCurrent);
        UtgStorageMinerQueryForm.setSize(pageSize);
        List<UtgStorageMinerDayVo> listInfo =UtgStorageMinerMapper.getMinerDayStatislist(UtgStorageMinerQueryForm);
        long total=UtgStorageMinerMapper.getMinerDayStatisCount(UtgStorageMinerQueryForm);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }

    public ResultMap<Page<UtgCltStoragedata>> pageUtgCltList(UtgStorageMinerQueryForm UtgStorageMinerQueryForm){
        Page page = UtgStorageMinerQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        UtgStorageMinerQueryForm.setCurrent(pageCurrent);
        UtgStorageMinerQueryForm.setSize(pageSize);
        List<UtgCltStoragedata> listInfo =UtgStorageMinerMapper.getUtgCltPageList(UtgStorageMinerQueryForm);
        long total=UtgStorageMinerMapper.getUtgCltTotal(UtgStorageMinerQueryForm);
        page.setRecords(listInfo);
        page.setTotal(total);
        return ResultMap.getSuccessfulResult(page);
    }


    /**
     * my/outLine
     * @param UtgStorageMinerQueryForm
     * @return
     */
    @Override
    public ResultMap<Map> outLine(UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        Map<String,Object> outLine = new HashMap<>();
        outLine.put("minerCount",UtgStorageMinerMapper.getTotal(UtgStorageMinerQueryForm));
        List<Addresses> addressList = accountMapper.selectSingleAddress(UtgStorageMinerQueryForm.getRevenue_address());
        if(addressList.size()>0){
            Addresses address = addressList.get(0);
            outLine.put("address",address.getAddress());
            outLine.put("balance",address.getBalance());
            outLine.put("srt_balance",address.getSrt_balance());
        }
        UtgStorageMiner n =  UtgStorageMinerMapper.getStatisByReAddress(UtgStorageMinerQueryForm);
        outLine.put("paysrt",n.getPaysrt());
        outLine.put("revenue_amount",n.getRevenue_amount());
        outLine.put("nodeCount",UtgStorageMinerMapper.getNodeCount(UtgStorageMinerQueryForm));

        BlockQueryForm blockQueryForm = new BlockQueryForm();
        blockQueryForm.setRevenueAddress(UtgStorageMinerQueryForm.getRevenue_address());
        blockQueryForm.setType(9);
        TransferMinerVo bdvo = transferMinerMapper.getLockSummaryByRevAddress(blockQueryForm);
        if(bdvo!=null){
            outLine.put("bdtotalamount",bdvo.getTotalAmount());
            outLine.put("bdreleaseamount",bdvo.getReleaseamount());
        }else{
            outLine.put("bdtotalamount",0);
            outLine.put("bdreleaseamount",0);
        }
        blockQueryForm.setType(1);
        TransferMinerVo blockvo = transferMinerMapper.getLockSummaryByRevAddress(blockQueryForm);
        if(blockvo!=null){
            outLine.put("blocktotalamount",blockvo.getTotalAmount());
            outLine.put("blockreleaseamount",blockvo.getReleaseamount());
        }else{
            outLine.put("blocktotalamount",0);
            outLine.put("blockreleaseamount",0);
        }
        return ResultMap.getSuccessfulResult(outLine);
    }


    @Override
    public AuthCfg getAuthCfgByAppid(String appid) {
        return UtgStorageMinerMapper.getAuthCfgByAppid(appid);
    }

    @Override
    public ResultMap<Area> getArea() {
        return ResultMap.getSuccessfulResult(UtgStorageMinerMapper.getArea());
    }

    @Override
    public ResultMap<OperatorConfig> getOperatorConfig() {
        return ResultMap.getSuccessfulResult(UtgStorageMinerMapper.getOperatorConfig());
    }

    @Override
    public ResultMap<Map> netBandWidth() {
        Map<String,Object> result = new HashMap<>();
        result.put("poolBandWidth",UtgStorageMinerMapper.getMinerSum().getBandwidth());
        return ResultMap.getSuccessfulResult(result);
    }

    @Override
    public ResultMap<Map> bandWidthToUtg(Long bandwidth) {
        Map<String,Object> result = new HashMap<>();

        UtgStorageMiner nm = UtgStorageMinerMapper.getMinerSum();
        BigDecimal storageTotal=nm.getBandwidth()==null?new BigDecimal("0"):nm.getBandwidth();

        BigDecimal Bandwidth=new BigDecimal(bandwidth);
        BigDecimal scale = new BigDecimal("10");
        long secondsPerYear     = 365 * 24 * 3600;
        long blockNumPerYear = secondsPerYear / 10;
        BigDecimal defaultAmount=new BigDecimal("125").multiply(new BigDecimal("10").pow(16)).divide(new BigDecimal("1048576"));
        BigDecimal mbPledgeNum = defaultAmount;   //1MB ? UTG
        long number=  blockMapper.getLeatestBlockNumber();//blocknumber
        BigDecimal  blockReward = UtgStorageMinerMapper.getAllBlockReward().divide(new BigDecimal(Constants.decimals),0,BigDecimal.ROUND_HALF_UP);
        BigDecimal  bandReward = UtgStorageMinerMapper.getAllBandWidthReward().divide(new BigDecimal(Constants.decimals),0,BigDecimal.ROUND_HALF_UP);
        BigDecimal flowHarvest= blockReward.add(bandReward);
        BigDecimal total=storageTotal;//
        if (number>blockNumPerYear) {
            BigDecimal calMbPledgeNum= flowHarvest.multiply(scale).divide(total.divide(new BigDecimal("1048576")));
            calMbPledgeNum=calMbPledgeNum.divide(new BigDecimal("1048576"),10,BigDecimal.ROUND_HALF_UP);
            if (calMbPledgeNum.compareTo(defaultAmount) < 0){
                mbPledgeNum = calMbPledgeNum;
            }else{
                mbPledgeNum = mbPledgeNum.divide(new BigDecimal(Constants.decimals));
            }
        }else{
            mbPledgeNum = mbPledgeNum.divide(new BigDecimal(Constants.decimals));
        }
        BigDecimal Amount =Bandwidth.multiply(mbPledgeNum);

        result.put("utg",Amount.toString());
        return ResultMap.getSuccessfulResult(result);
    }

    public  ResultMap  getMinnnerPledgeStatus(UtgStorageMinerQueryForm UtgStorageMinerQueryForm){
        UtgStorageMiner vo = UtgStorageMinerMapper.getSingleMiner(UtgStorageMinerQueryForm.getMiner_addr());
        if(vo!=null){
            return ResultMap.getSuccessfulResult(vo.getMiner_status());
        }else{
            return ResultMap.getFailureResult("query no data");
        }
    }

    @Override
    public ResultMap<Map> getUtgNetStatics(UtgNetStaticsForm net) {
        String endTime = net.getEndTime();
        Date end = null;
        if(StringUtils.isEmpty(endTime)){
            end = new Date();
            endTime = DateUtil.getFormatDateTime(end, "yyyy-MM-dd");
        }else{
            endTime = endTime.substring(0,10);
            end = DateUtil.parseDate(endTime, "yyyy-MM-dd");
        }
        String startTime = net.getStartTime();
        if(StringUtils.isEmpty(startTime)){
            startTime = DateUtil.getFormatDateTime(DateUtil.addDaysToDate(end,-30), "yyyy-MM-dd");
        }else{
            startTime = startTime.substring(0,10);
        }        
//        List<UtgNetStatics> list = UtgStorageMinerMapper.queryUtgNetStaticsBetwentime(startTime,endTime);
//        for(UtgNetStatics uns : list){
//        	Date date = uns.getCtime();
//        	if(date!=null){
//        		uns.setTimestamp(date.getTime());
//        	}
//        }
//        return ResultMap.getSuccessfulResult(list);
        List<StorageGlobalStat> list = storageStatMapper.queryStorageGlobalStat(startTime,endTime);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONArray array = new JSONArray();
        for(StorageGlobalStat item:list){
        	JSONObject json = new JSONObject();
        	json.put("ctime", df.format(item.getBlocktime()));
        	json.put("timestamp", item.getBlocktime().getTime());
        	json.put("blocknumber", item.getBlocknumber());
        	json.put("total_bw", item.getStorage_space());
        	json.put("incre_bw", item.getIncrease_space());
        	json.put("total_rent", item.getRent_space());
        	json.put("incre_rent", item.getIncrease_rent());
        	array.add(json);
        }
        return ResultMap.getSuccessfulResult(array);
    }

    @Override
    public ResultMap<Map> getNetServiceRank(UtgStorageMinerQueryForm UtgStorageMinerQueryForm) {
        if(StringUtils.isEmpty(UtgStorageMinerQueryForm.getTime())){
            String day =  DateUtil.getFormatDateTime( DateUtil.getBeginDayOfYesterday(), "yyyy-MM-dd");
            UtgStorageMinerQueryForm.setTime(day);
        }
        BigDecimal totalBw = UtgStorageMinerMapper.getMinerSum().getBandwidth();
        UtgStorageMinerQueryForm.setBandwidth(totalBw);
        Page page = UtgStorageMinerQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        UtgStorageMinerQueryForm.setCurrent(pageCurrent);
        UtgStorageMinerQueryForm.setSize(pageSize);
        long total=UtgStorageMinerMapper.getNetServiceRankTotal(UtgStorageMinerQueryForm);
        page.setTotal(total);
        if(total>0){
            List<NetRankVo> listInfo =UtgStorageMinerMapper.getNetServiceRankList(UtgStorageMinerQueryForm);
            page.setRecords(listInfo);
        }
        return ResultMap.getSuccessfulResult(page);
    }
}
