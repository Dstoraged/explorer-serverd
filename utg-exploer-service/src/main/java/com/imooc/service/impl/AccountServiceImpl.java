package com.imooc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.mapper.AccountMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.pojo.Addresses;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.vo.AddressVo;
import com.imooc.service.AccountService;
import com.imooc.utils.ResultMap;
import com.imooc.utils.Web3jUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl  implements AccountService {
    private static final Logger logger= LoggerFactory.getLogger(AccountServiceImpl.class);
    @Autowired
    private AccountMapper accountMapper;

    @Value("${FROM_ADDRESS_HASH}")
    private String addressHash;

    @Value("${proportion}")
    private  String proportion;
    
    @Value("${ipaddress}")
    private String ipaddress;
    
    @Value("${remote.server.url:}")
    private String remoteServerUrl;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TransactionMapper transactionMapper;
    public ResultMap getSingleAddressBalance(String address) {
        List<Addresses> listInfo =accountMapper.selectSingleAddress(address);

        return ResultMap.getSuccessfulResult(listInfo);
    }

    @Override
    public ResultMap getbalancemulti(String address) {
        String [] hashParams=address.split(",");
        logger.info("==========>"+hashParams);
        List<String> addressList =accountMapper.getMultipleByHash(hashParams);
        if(addressList.size()>0 && addressList!=null){
            logger.info("====>"+addressList);
            return ResultMap.getSuccessfulResult(addressList);
        }else{
            return ResultMap.getFailureResult("");
        }
    }

    @Override
    public ResultMap<Page<AddressVo>> pageList(BlockQueryForm blockQueryForm) {
        BigDecimal value= new BigDecimal(proportion);
        Page page =blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        String addr = blockQueryForm.getAddress();
        List<AddressVo> listInfo =accountMapper.getPageList(pageCurrent,pageSize,addr);
        if(listInfo.size()==0){
            return ResultMap.getSuccessfulResult(null);
        }
      /*  NumberFormat percent = NumberFormat.getPercentInstance();
        BigDecimal balance= vo.getBalance();
               BigDecimal c=balance.divide(value,8,BigDecimal.ROUND_HALF_UP);
                percent.setMaximumFractionDigits(4);
               vo.setProportion(percent.format(c.doubleValue()));
        percent.setMaximumFractionDigits(2);*/
        if(listInfo.size()>0 || listInfo!=null){
            for(AddressVo vo:listInfo){
                 String address=vo.getAddress();
                 long toCount=transactionMapper.getTotalTransactionToCount(address);
                 //vo.setToCount(toCount);
                // vo.setNonce(toCount);
                vo.setToCount(toCount);
            }
            long total = accountMapper.getTotal(addr);
            page.setRecords(listInfo);
            page.setTotal(total);
            return  ResultMap.getSuccessfulResult(page);
        }else{
            return ResultMap.getSuccessfulResult(null);
        }
    }



    @Override
    public ResultMap<Page<Addresses>> getContractPageTokenList(BlockQueryForm blockQueryForm) {
        Page page =blockQueryForm.newFormPage();
        Long pageSize=page.getSize();
        Long pageCurrent =page.getCurrent();
        pageCurrent=(pageCurrent-1)*pageSize;
        blockQueryForm.setCurrent(pageCurrent);
        blockQueryForm.setSize(pageSize);
        String contractAddress=blockQueryForm.getAddress();
        List<AddressVo> listInfo =accountMapper.getContractPageTokenList(blockQueryForm);
        long count =accountMapper.getContractTokenCount(contractAddress);
        page.setRecords(listInfo);
        page.setTotal(count);
        return ResultMap.getSuccessfulResult(page);
    }

    public ResultMap  getAddressVoByaddress(String address){
        AddressVo vo = accountMapper.getAddressVoByaddress(address);
        if(vo!=null){
            return ResultMap.getSuccessfulResult(vo.getSrt_balance().toString());
        }else{
            return ResultMap.getFailureResult("query no data");
        }
    }
    
    @Override
    public BigDecimal getAddressBalance(String addressHash){
    	Web3j web3j = Web3jUtils.getWeb3j(ipaddress);     
		BigInteger balance = BigInteger.ZERO;			
		try {        
			balance = web3j.ethGetBalance(addressHash, DefaultBlockParameterName.LATEST).send().getBalance();
			Long txcount = web3j.ethGetTransactionCount(addressHash, DefaultBlockParameterName.LATEST).send().getTransactionCount().longValue();
			Addresses address = accountMapper.getAddressInfo(addressHash);
			if(address!=null){
				if (new BigDecimal(balance).compareTo(address.getBalance()) != 0  || !txcount.equals(address.getNonce())) {
					if(remoteServerUrl!=null && !remoteServerUrl.trim().equals("")){
						String url = remoteServerUrl+"/platform/getAddressBalance?addressHash="+addressHash;						
						String resultString = restTemplate.getForObject(url, String.class);
						log.info("Remote get "+url+" ,result="+resultString);						
					}else{
						address.setBalance(new BigDecimal(balance));
						address.setNonce(txcount);
						accountMapper.saveOrUpdata(address);
					}
				}
			}
		} catch (Exception e) {
			log.warn("ethGetBalance address "+addressHash+",error:"+e.getMessage());
		}
		return new BigDecimal(balance);
	}
}
