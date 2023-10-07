package com.imooc.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.DposNodeMapper;
import com.imooc.mapper.DposVoterWalletMapper;
import com.imooc.mapper.DposVotesMapper;
import com.imooc.pojo.DposNode;
import com.imooc.pojo.DposVotes;
import com.imooc.pojo.DposVotesWallet;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.Form.DposVoterQueryForm;
import com.imooc.pojo.vo.DposVoterVo;
import com.imooc.service.DposVoterService;
import com.imooc.utils.ResultMap;
import com.imooc.utils.Web3jUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.utils.Collection;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DposVoterImpl implements DposVoterService {

    @Autowired
    private DposVotesMapper dposVotesMapper;

    @Autowired
    private DposNodeMapper dposNodeMapper;

    @Autowired
    private DposVoterWalletMapper dposVoterWalletMapper;

    @Value("${ipaddress}")
    private String ipaddress;

    @Value("${round}")
    private Long round;

    @Value("${serverTime}")
    private Long serverTime;



    public ResultMap<Page<DposVotes>> getDposVoterInfo(BlockQueryForm blockQueryForm)throws IOException {
        List<DposVotes> maps =new ArrayList<>();
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        Integer status =blockQueryForm.getStatus();
        List<DposVotes> listInfo=new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        BigInteger avgBlockTime=null;
        BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        Long nowNumber= blockNumber.longValue();
       /* AlienSnapshot.Snapshot snap=web3j.alientSnapshotByNumber(new Long(nowNumber).intValue()).send().getSnapshot();//
        if(snap.getPeriod()!=null){
            avgBlockTime=snap.getPeriod();
        }else{
            avgBlockTime= new BigInteger("20");
        }*/
        avgBlockTime= new BigInteger("20");
        long timeStampOne=1L;
        BigInteger timeOne = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(timeStampOne)), true).send().getBlock().getTimestamp();
        if(1==status){
            maps=getVotesMap(web3j);
            int size=maps.size();
            int pages=(int)page.getCurrent();
            int count=(int)page.getSize();
            int fromIndex=count*(pages-1);
            int toIndex=fromIndex+count;
            if(toIndex>=size){
                toIndex=size;
            }
            page.setTotal(maps.size());
            Collections.sort(maps, new Comparator<DposVotes>() {
                public int compare(DposVotes o1, DposVotes o2) {
                    int i= (int) (o2.getBlockNumber() -o1.getBlockNumber());
                    if(i==0){
                        return (int) (o2.getBlockNumber()-o1.getBlockNumber());
                    }
                    return i;
                }
            });
            maps=maps.subList(fromIndex,toIndex);
            page.setRecords(maps);
            return ResultMap.getSuccessfulResult(page);
        }else{

            Integer round = blockQueryForm.getRound();
            listInfo = dposVotesMapper.selectVoterInfo(round, pageCurrent, pageSize);
            for(DposVotes dp:listInfo){
                dp.setAvgBlockTime(avgBlockTime);
                dp.setTimeStampOne(timeOne.longValue()*1000);
            }
            long total = dposVotesMapper.getTotalCountForRound(round);
            page.setRecords(listInfo);
            page.setTotal(total);
        }
        return ResultMap.getSuccessfulResult(page);
    }



    @Override
    public ResultMap<Page<DposVoterVo>> getVoterListInfo(BlockQueryForm blockQueryForm) throws IOException {
        List<DposVoterVo> list=new ArrayList<>();
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        Long nowNumber= blockNumber.longValue();
        DposVoterVo vo = null;
        if(nowNumber<=round){
            if(pageCurrent>1){
                page.setRecords(null);
                page.setTotal(0);
                return ResultMap.getSuccessfulResult(page);
            }
            BigInteger maxTime=BigInteger.valueOf(serverTime);
            vo = getDposVo(web3j);
            vo.setMaxTime(maxTime);
            list.add(vo);
            page.setRecords(list);
            page.setTotal(list.size());
            return ResultMap.getSuccessfulResult(page);
        }else{
            vo = getDposVo(web3j);
            list = dposVotesMapper.getVoterPageList();
            long timeStampOne=1L;
            BigInteger timeOne = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(timeStampOne)), true).send().getBlock().getTimestamp();
            for (DposVoterVo dp : list) {
                long blockminNumber = dp.getMinBlock();
                BigInteger minTime = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockminNumber)), true).send().getBlock().getTimestamp();
                BigInteger maxTime=BigInteger.valueOf(serverTime);
                dp.setMinTime(minTime);
                dp.setMaxTime(maxTime);
                dp.setRoundNumber(round);
                dp.setNowBlock(blockNumber.longValue());
                dp.setTimeStampOne(timeOne.longValue()*1000);
            }
            list.add(vo);
            int size=list.size();
            int pages=(int)page.getCurrent();
            int count=(int)page.getSize();
            int fromIndex=count*(pages-1);
            int toIndex=fromIndex+count;
            if(toIndex>=size){
                toIndex=size;
            }
            page.setTotal(list.size());
            Collections.sort(list, new Comparator<DposVoterVo>() {
                public int compare(DposVoterVo o1, DposVoterVo o2) {
                    int i= (int) (o2.getRound() -o1.getRound());
                    if(i==0){
                        return (int) (o2.getRound()-o1.getRound());
                    }
                    return i;
                }
            });
            list=list.subList(fromIndex,toIndex);
            page.setRecords(list);
            long totalCount = dposVotesMapper.getVoterPageCount();
            page.setRecords(list);
            page.setTotal(totalCount+1);
            return ResultMap.getSuccessfulResult(page);
        }
    }


    @Override
    public ResultMap<Page<DposNode>> getDposNodeInfo(BlockQueryForm blockQueryForm)throws IOException {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        List<DposNode> maps =new ArrayList<>();
        List<DposNode> listInfo=new ArrayList<>();
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        Integer rount= blockQueryForm.getRound();
        BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        int nowNumber=blockNumber.intValue();
        Integer rounds=getRound(nowNumber);
        if(rount==rounds){
            maps=getNodesMap(web3j);
            int size=maps.size();
            int pages=(int)page.getCurrent();
            int count=(int)page.getSize();
            int fromIndex=count*(pages-1);
            int toIndex=fromIndex+count;
            if(toIndex>=size){
                toIndex=size;
            }
            Collections.sort(maps, new Comparator<DposNode>() {
                public int compare(DposNode o1, DposNode o2) {
                    int i=0;
                    if(o1.getStake()!=null && o2.getStake()!=null){
                        i= o2.getStake().compareTo(o1.getStake());
                        if(i==0){
                            return 0;
                        }
                    }
                    return i;
                }
            });
            page.setTotal(maps.size());
            maps=maps.subList(fromIndex,toIndex);
            page.setRecords(maps);
            return ResultMap.getSuccessfulResult(page);
        }else{
            listInfo = dposNodeMapper.selectVoterNodeInfo(rount, pageCurrent, pageSize);
            long total = dposNodeMapper.getTotalCountForRound(rount);
            page.setRecords(listInfo);
            page.setTotal(total);
            return ResultMap.getSuccessfulResult(page);
        }
    }

    private List<DposNode> getNodesMapForWallet(Web3j web3j, String address)throws IOException  {

        return null;
    }

    @Override
    public ResultMap<Page<DposVotes>> getDposNodeForAddress(BlockQueryForm blockQueryForm)throws IOException {
        return null;
    }

    @Override
    public ResultMap getDposVoterTotalForRound() throws IOException {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        BigInteger number= web3j.ethBlockNumber().send().getBlockNumber();
        Integer num= Integer.valueOf(number.toString());
        Integer round= getRound(num);
        DposVoterVo vo= getDposVo( web3j);
        BigDecimal total=vo.getVoterTotal();
        Map<String,Object> map =new HashMap<String,Object>();
        map.put("total",total);
        map.put("round",round);
        return ResultMap.getSuccessfulResult(map);
    }


    private  Integer getRound(int blockNumber){
        int rouds=0;
        if(blockNumber<=round){
            rouds=1;
            return rouds;
        }else{
            float cell=blockNumber/round;
            double nYear=(double)blockNumber/round;
            double nFee= Math.ceil(nYear);
            int round = new Double(nFee).intValue();
            return round;
        }
    }
    private DposVoterVo getDposVo(Web3j web3j)throws IOException {
        return null;
    }

    private List<DposVotes>  getVotesMap(Web3j web3j)throws IOException{
        return null;
    }


    private List<DposNode>  getNodesMap(Web3j web3j)throws IOException{
        return null;
    }

    private List<DposVotes>  getVotesMapContinesAddress(Web3j web3j,String address)throws IOException{
        return null;
    }

    @Override
    public ResultMap getDposVoterAddressTotal(BlockQueryForm blockQueryForm)throws IOException {

        return null;
    }


    public ResultMap<Page<DposNode>> getDposNodeInfoWallet(BlockQueryForm blockQueryForm)throws IOException {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        List<DposNode> maps =new ArrayList<>();
        List<DposNode> listInfo=new ArrayList<>();
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        Integer status =blockQueryForm.getStatus();
        String address=blockQueryForm.getAddress();
        Integer rount= blockQueryForm.getRound();
        BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        int nowNumber=blockNumber.intValue();
        Integer rounds=getRound(nowNumber);
        if(rount==rounds){
            if(!address.equals("") ||address.length()!=0){
                maps=getNodesMapForWallet(web3j,address);
            }else{
                maps=getNodesMap(web3j);
            }
            int size=maps.size();
            int pages=(int)page.getCurrent();
            int count=(int)page.getSize();
            int fromIndex=count*(pages-1);
            int toIndex=fromIndex+count;
            if(toIndex>=size){
                toIndex=size;
            }
            page.setTotal(maps.size());
            maps=maps.subList(fromIndex,toIndex);
            page.setRecords(maps);
            return ResultMap.getSuccessfulResult(page);
        }else{
            if(!address.equals("") ||address.length()!=0){
                listInfo = dposNodeMapper.getHistoryNodeInfo(rount, pageCurrent, pageSize,address);
                long total = dposNodeMapper.getHistoryNodeTotal(rount,address);
                page.setRecords(listInfo);
                page.setTotal(total);
                return ResultMap.getSuccessfulResult(page);
            }else{
                listInfo = dposNodeMapper.selectVoterNodeInfo(rount, pageCurrent, pageSize);
                long total = dposNodeMapper.getTotalCountForRound(rount);
                page.setRecords(listInfo);
                page.setTotal(total);
            }
            return ResultMap.getSuccessfulResult(page);
        }
    }


    @Override
    public ResultMap<Page<DposVotes>> getDposByRoundAndCandidateForAddress(BlockQueryForm blockQueryForm) throws Exception {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        BigInteger nowNumber= web3j.ethBlockNumber().send().getBlockNumber();
        int numbers= nowNumber.intValue();
        List<DposVotes> list=new ArrayList<>();
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        Integer round=getRound(numbers);
        String candidateAddress= blockQueryForm.getCandidate();
        candidateAddress=candidateAddress.trim();
        String address=blockQueryForm.getAddress();
        address=address.trim();
        Integer paramRound=blockQueryForm.getRound();
        if(round !=null &&round<=1) {
            list = getVotesMapContinesAddressAndCandidateAddress(web3j, candidateAddress,address);
            if(paramRound==round){
                list = getVotesMapContinesAddressAndCandidateAddress(web3j, candidateAddress,address);
            }
            if(paramRound>round){
                return ResultMap.getSuccessfulResult(null);
            }
            int size = list.size();
            int pages = (int) page.getCurrent();
            int count = (int) page.getSize();
            int fromIndex = count * (pages - 1);
            int toIndex = fromIndex + count;
            if (toIndex >= size) {
                toIndex = size;
            }
            if (pages > 1 && fromIndex > toIndex) {
                page.setRecords(null);
                page.setTotal(0);
                return ResultMap.getSuccessfulResult(page);
            }
            if (pages > list.size()) {
                page.setRecords(null);
                page.setTotal(0);
                return ResultMap.getSuccessfulResult(page);
            }
            page.setTotal(list.size());
            Collections.sort(list, new Comparator<DposVotes>() {
                public int compare(DposVotes o1, DposVotes o2) {
                    int i = (int) (o2.getBlockNumber() - o1.getBlockNumber());
                    if (i == 0) {
                        return (int) (o2.getBlockNumber() - o1.getBlockNumber());
                    }
                    return i;
                }
            });
            list = list.subList(fromIndex, toIndex);
            page.setTotal(list.size());
            page.setRecords(list);
            return ResultMap.getSuccessfulResult(page);
        }else{
            if(!candidateAddress.equals("") ||candidateAddress.length()!=0){
                if(!address.equals("") ||address.length()!=0){
                    List<DposVotes> listInfo=getVotesMapContinesAddressAndCandidateAddress(web3j, candidateAddress,address);
                    list = dposVotesMapper.selectVoterNodeAddress(blockQueryForm);
                    list.addAll(listInfo);
                    int size=listInfo.size();
                    int pages=(int)page.getCurrent();
                    int count=(int)page.getSize();
                    int fromIndex=count*(pages-1);
                    int toIndex=fromIndex+count;
                    if(toIndex>=size){
                        toIndex=size;
                    }
                    if(pages>listInfo.size()){
                        page.setRecords(null);
                        page.setTotal(0);
                        return ResultMap.getSuccessfulResult(page);
                    }
                    if(pages>1 &&fromIndex>toIndex){
                        page.setRecords(null);
                        page.setTotal(0);
                        return ResultMap.getSuccessfulResult(page);
                    }
                    Collections.sort(list, new Comparator<DposVotes>() {
                        public int compare(DposVotes o1, DposVotes o2) {
                            int i= (int) (o2.getBlockNumber() -o1.getBlockNumber());
                            if(i==0){
                                return (int) (o2.getBlockNumber()-o1.getBlockNumber());
                            }
                            return i;
                        }
                    });
                    list=list.subList(fromIndex,toIndex);
                    page.setTotal(list.size());
                    page.setRecords(list);
                    return ResultMap.getSuccessfulResult(page);
                }
            }else{
                return ResultMap.getSuccessfulResult(null);
            }
        }
        return ResultMap.getSuccessfulResult(null);
    }



    private List<DposVotes> getVotesMapContinesCandidateAddress(Web3j web3j, String candidateAddress)  throws Exception {

        return null;
    }

    private List<DposVotes> getVotesMapContinesAddressAndCandidateAddress(Web3j web3j, String candidateAddress,String address)  throws Exception {

        return null;
    }

    @Override
    public ResultMap<Page<DposVotes>> getDposVoterForCandidate(BlockQueryForm blockQueryForm) throws Exception {
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        BigInteger nowNumber= web3j.ethBlockNumber().send().getBlockNumber();
        int numbers= nowNumber.intValue();
        List<DposVotes> list=new ArrayList<>();
        Page page = blockQueryForm.newFormPage();
        Long pageSize = page.getSize();
        Long pageCurrent = page.getCurrent();
        pageCurrent = (pageCurrent - 1) * pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        Integer round=getRound(numbers);
        String candidateAddress= blockQueryForm.getCandidate();
        candidateAddress=candidateAddress.trim();
        Integer paramRound=blockQueryForm.getRound();
        if(round !=null &&round<=1) {
            list = getVotesMapContinesCandidateAddress(web3j, candidateAddress);
            if(paramRound==round){
                list = getVotesMapContinesCandidateAddress(web3j, candidateAddress);
            }
            if(paramRound>round){
                return ResultMap.getSuccessfulResult(null);
            }
            int size = list.size();
            int pages = (int) page.getCurrent();
            int count = (int) page.getSize();
            int fromIndex = count * (pages - 1);
            int toIndex = fromIndex + count;
            if (toIndex >= size) {
                toIndex = size;
            }
            if (pages > 1 && fromIndex > toIndex) {
                page.setRecords(null);
                page.setTotal(0);
                return ResultMap.getSuccessfulResult(page);
            }
            if (pages > list.size()) {
                page.setRecords(null);
                page.setTotal(0);
                return ResultMap.getSuccessfulResult(page);
            }
            page.setTotal(list.size());
            Collections.sort(list, new Comparator<DposVotes>() {
                public int compare(DposVotes o1, DposVotes o2) {
                    int i = (int) (o2.getBlockNumber() - o1.getBlockNumber());
                    if (i == 0) {
                        return (int) (o2.getBlockNumber() - o1.getBlockNumber());
                    }
                    return i;
                }
            });
            list = list.subList(fromIndex, toIndex);
            page.setTotal(list.size());
            page.setRecords(list);
            return ResultMap.getSuccessfulResult(page);
        }else{
            if(!candidateAddress.equals("") ||candidateAddress.length()!=0){
                List<DposVotes> listInfo=getVotesMapContinesCandidateAddress(web3j, candidateAddress);
                list = dposVotesMapper.selectVoterNodeAddress(blockQueryForm);
                list.addAll(listInfo);
                int size=listInfo.size();
                int pages=(int)page.getCurrent();
                int count=(int)page.getSize();
                int fromIndex=count*(pages-1);
                int toIndex=fromIndex+count;
                if(toIndex>=size){
                    toIndex=size;
                }
                if(pages>listInfo.size()){
                    page.setRecords(null);
                    page.setTotal(0);
                    return ResultMap.getSuccessfulResult(page);
                }
                if(pages>1 &&fromIndex>toIndex){
                    page.setRecords(null);
                    page.setTotal(0);
                    return ResultMap.getSuccessfulResult(page);
                }
                Collections.sort(list, new Comparator<DposVotes>() {
                    public int compare(DposVotes o1, DposVotes o2) {
                        int i= (int) (o2.getBlockNumber() -o1.getBlockNumber());
                        if(i==0){
                            return (int) (o2.getBlockNumber()-o1.getBlockNumber());
                        }
                        return i;
                    }
                });
                list=list.subList(fromIndex,toIndex);
                page.setTotal(list.size());
                page.setRecords(list);

                return ResultMap.getSuccessfulResult(page);
            }else{
                return ResultMap.getSuccessfulResult(null);
            }
        }

    }

    @Override
    public ResultMap<DposVoterVo> getVoterListInfoForRound(BlockQueryForm blockQueryForm) throws Exception{
        Web3j web3j = Web3jUtils.getWeb3j(ipaddress);
        DposVoterVo vo = new DposVoterVo();
        Integer paramRound=blockQueryForm.getRound();
        BigInteger blockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        Integer nowRound=getRound(blockNumber.intValue());
        if(paramRound==nowRound){
            vo=getDposVo(web3j);
        }else{
            long timeStampOne=1L;
            BigInteger timeOne = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(timeStampOne)), true).send().getBlock().getTimestamp();
            BigInteger maxTime=BigInteger.valueOf(serverTime);
            vo=dposVotesMapper.getVoterInfo(paramRound);
            vo.setTimeStampOne(timeOne.longValue()*1000);
            vo.setRoundNumber(round);
            vo.setRound(paramRound);
            vo.setNowBlock(blockNumber.longValue());
            vo.setMaxTime(maxTime);
        }
        return ResultMap.getSuccessfulResult(vo);
    }

    @Override
    public ResultMap<Page<DposVotesWallet>> getDposVoterWallet(BlockQueryForm blockQueryForm) throws Exception {
        return null;
    }

    @Override
    public ResultMap<Page<DposVotesWallet>> getVotersInfoPageList(DposVoterQueryForm dposVoterQueryForm) {
        List<DposVotesWallet> listInfo =dposVoterWalletMapper.getVoterInfoPageList(dposVoterQueryForm);
        if(listInfo.size()>0 ||!listInfo.isEmpty()){
            return ResultMap.getSuccessfulResult(listInfo);
        }else{
            return ResultMap.getSuccessfulResult(null);
        }
    }

}
