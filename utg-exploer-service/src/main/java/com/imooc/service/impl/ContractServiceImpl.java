package com.imooc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.io.Files;
import com.imooc.mapper.ContractLockupMapper;
import com.imooc.mapper.ContractMapper;
import com.imooc.mapper.ContractSourceMapper;
import com.imooc.mapper.ContractVersionMapper;
import com.imooc.mapper.ContractverifiedMapper;
import com.imooc.mapper.TransactionMapper;
import com.imooc.pojo.Contract;
import com.imooc.pojo.ContractLockup;
import com.imooc.pojo.ContractSource;
import com.imooc.pojo.ContractVersion;
import com.imooc.pojo.Contractverified;
import com.imooc.pojo.Transaction;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.service.AccountService;
import com.imooc.service.ContractService;
import com.imooc.utils.CommadUtil;
import com.imooc.utils.ResultMap;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContractServiceImpl implements ContractService {
	
	@Autowired
	ContractMapper contractMapper;
	
	@Autowired
	ContractSourceMapper contractSourceMapper;
	
	@Autowired
	ContractVersionMapper contractVersionMapper;
	
	@Autowired
	ContractLockupMapper contractLockupMapper;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	TransactionMapper transactionMapper;
	
	

	@Override
	public ResultMap<Page<Contract>> getContractList(BlockQueryForm blockQueryForm) {
		Page<Contract> page = blockQueryForm.newFormPage();
		Long pageSize = page.getSize();
		Long pageCurrent = page.getCurrent();
		pageCurrent = (pageCurrent - 1) * pageSize;
		blockQueryForm.setCurrent(pageCurrent);
		List<Contract> list = contractMapper.getPageList(blockQueryForm);
		for(Contract contract:list){
//			BigDecimal balance = accountService.getAddressBalance(contract.getContract());
//			contract.setBalance(balance);
			long txcount = transactionMapper.getTotalTransactionToCount(contract.getContract());
			contract.setTxcount(txcount);
		}
		long total = contractMapper.getTotal(blockQueryForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}

	@Override
	public ResultMap<Contract> getContractInfo(String contractAddr) {		
		Contract contract = contractMapper.getContract(contractAddr);
		if(contract!=null){
			BigDecimal balance = accountService.getAddressBalance(contractAddr);
			contract.setBalance(balance);
			long txcount = transactionMapper.getTotalTransactionToCount(contract.getContract());
			contract.setTxcount(txcount);
			return ResultMap.getSuccessfulResult(contract);
		}		
		return ResultMap.getFailureResult("query no data");
	}
		
	@Override
	public ResultMap<List<ContractVersion>> getContractVersions(){
		List<ContractVersion> list = contractVersionMapper.getContractVersionList();
		return ResultMap.getSuccessfulResult(list);
	}
	
	@Override
	public ResultMap<List<ContractSource>> getContractSources(String contractAddr) {
		List<ContractSource> list = contractSourceMapper.getContractSources(contractAddr);
		return ResultMap.getSuccessfulResult(list);		
	}
		
	@Override
	public ResultMap<?> verifyContractSources(String contractAddr, String contractName, String version, MultipartFile[] files) {
		if(files.length==0)
			return ResultMap.getFailureResult("Files is empty");
		ContractVersion contractVersion = contractVersionMapper.getContractVersion(version);
		if(contractVersion==null)
			return ResultMap.getFailureResult("Unsupported version "+version);
		String metadata_bytecode = contractVersion.getMetadata_bytecode();
		
		Contract contract = contractMapper.getContract(contractAddr);
		if(contract==null)
			return ResultMap.getFailureResult("Contract "+contractAddr+" is not exist");
		if(contract.getVerified()!=null && contract.getVerified()==1)
			return ResultMap.getFailureResult("Contract "+contractAddr+" has verified");
		String txhash = contract.getTxhash();
		if(StringUtils.isEmpty(txhash))
			return ResultMap.getFailureResult("Contract "+contractAddr+" has no transaction data");		
		Transaction tx = transactionMapper.getTransactionByTxHash(txhash);	
		if(tx==null)
			return ResultMap.getFailureResult("Contract "+contractAddr+" transaction "+txhash+" is not exist");
		
		String input = tx.getInput();
		if(input==null)
			return ResultMap.getFailureResult("Invaild contract input data");
		if(!input.contains(metadata_bytecode)){
			log.warn("Contract input :\n"+input+"\n that version "+version+" does not contains metadata :"+metadata_bytecode);
			return ResultMap.getFailureResult("Contract input data does not contains metadata");
		}
		input = input.substring(2);
		String rootpath = System.getProperty("user.dir");	//getClass().getResource("/").getPath();
		rootpath = rootpath + "/data/" ;					//	/data/explorerapi/data/	
		List<ContractSource> sourceList = new ArrayList<>();
		int ord=0;
		for(MultipartFile mpFile :files){			
			String filename = mpFile.getOriginalFilename();			
			String filepath = rootpath + "contracts/" + contractAddr + "/" + filename;	//{rootpath}/contracts/{contractAddr}/{filename}			
			String filesrc = null;
			try {
				File file = new File(filepath);
				if(!file.exists()){
					File dir = file.getParentFile();
					if(!dir.exists())
						dir.mkdirs();
					file.createNewFile();
				}
				byte[] bytes = mpFile.getBytes();
				filesrc = new String(bytes,"UTF-8");				
				Files.write(bytes,file);				
			} catch (IOException e) {
				log.warn("Write Contract file "+filename+" failed:",e);
				return ResultMap.getFailureResult("Write Contract file "+filename+" failed:"+e.getMessage());				
			}
			ContractSource contractSource = new ContractSource();
			contractSource.setContract(contractAddr);
			contractSource.setFilename(filename);
			contractSource.setSourcecode(filesrc);
			contractSource.setFilepath(filepath);
			contractSource.setOrd(ord);
			sourceList.add(contractSource);
			ord++;
		}
		
		boolean verified = false;
		for(ContractSource contractSource : sourceList){
		//	String filepath = rootpath + "contracts/" + contractAddr + "/" + filename;	//{rootpath}/contracts/{contractAddr}/{filename}
			String filepath = contractSource.getFilepath();
			String command = rootpath + "solc-static-linux/"+ version; 	//{rootpath}/solc-static-linux/{version}
						
			String result = null;
			JSONObject resultJson;
			try{
				result = CommadUtil.exec(command,"--combined-json","abi,bin",filepath);
				log.info("Execute command :" + command + " --combined-json abi,bin " + filepath + "\n result:" + result);
				if(result==null||"".equals(result)){
					log.warn("Execute command :" + command + " --combined-json abi,bin " + filepath + " error:" + result);
					return ResultMap.getFailureResult("Verification failed : result is empty");
				}
				resultJson = JSONObject.parseObject(result);								
			}catch(RuntimeException e){
				log.warn("Execute Contract verify command error",e);
				return ResultMap.getFailureResult("Verification error:"+e.getMessage());
			}
						
			JSONObject binJson = new JSONObject();
			JSONObject abiJson = new JSONObject();
			JSONObject contractsJson = resultJson.getJSONObject("contracts");
			for(String key : contractsJson.keySet()){
				String method = key.indexOf(":")>0 ? key.substring(key.lastIndexOf(":")+1) : null;
				JSONObject methodJson = contractsJson.getJSONObject(key);
				String bin = methodJson.getString("bin");				
				if (method!=null && method.equalsIgnoreCase(contractName.trim())) {
					if (!bin.contains(metadata_bytecode)){
						log.warn("Contract source bin :\n"+bin+"\n that version "+version+" does not contains metadata :"+metadata_bytecode);
						return ResultMap.getFailureResult("Verification failed : source does contains medadata");
					}
					String binProcessed = bin.substring(0, bin.indexOf(metadata_bytecode));
					String inputProcessed = input.substring(0, input.indexOf(metadata_bytecode));
					if(!binProcessed.equalsIgnoreCase(inputProcessed)){	
						log.warn("Source data :\n" + bin + "\n input data :\n" + input + "\n metadata prefix:" + metadata_bytecode);
						log.warn("Processed Source data :\n"+binProcessed+"\n is inconsistent with processed input data :\n"+inputProcessed);
						return ResultMap.getFailureResult("Verification failed : source data is inconsistent with input");
					}
					verified = true;
					contractSource.setOrd(-1);
				}
				binJson.put(method, bin);
				abiJson.put(method, methodJson.getJSONArray("abi"));
			}
			contractSource.setBin(JSON.toJSONString(binJson, true));
			contractSource.setAbi(JSON.toJSONString(abiJson, true));			
		}		
		if(!verified)
			return ResultMap.getFailureResult("Verification failed : cannot find "+contractName+" in source files");		
		sourceList = sourceList.stream().sorted((a, b) -> a.getOrd().compareTo(b.getOrd())).collect(Collectors.toList());		
		
		for(int i=0;i<sourceList.size();i++){
			ContractSource contractSource = sourceList.get(i);
			contractSource.setOrd(i);
			contractSourceMapper.saveOrUpdate(contractSource);			
		}		
		contract.setContractname(contractName);
		contract.setVerified(1);
		contract.setVersion(version);
		contract.setVerifydate(new Date());
		contractMapper.updateVerify(contract);		
		return ResultMap.getSuccessfulResult("ok");
	}
	
	@Override
	public ResultMap<?> getLockupContracts(String address,String admin) {
		List<Contract> contracts;
		if(StringUtils.isNotBlank(address))
			contracts = contractMapper.getAuthorizedContracts(address);
		else if(StringUtils.isNotBlank(admin))
			contracts = contractMapper.getAuthorizedContracts2(admin);
		else 
			throw new RuntimeException("Required query param address or admin");
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(Contract contract : contracts){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("contract", contract.getContract());
			map.put("name", contract.getName());
			map.put("symbol", contract.getSymbol());
		//	map.put("balance", contract.getTotalSupply());
			if(address!=null){
				int role = 3;
				if (address.equals(contract.getAdmin1()))
					role = 1;
				else if (address.equals(contract.getAdmin2()))
					role = 2;
				map.put("role", role);
			}
			map.put("lockupperiod", contract.getLockupperiod());
			map.put("releaseperiod", contract.getReleaseperiod());
			map.put("releaseinterval", contract.getReleaseinterval());
			list.add(map);
		}
		return ResultMap.getSuccessfulResult(list);		
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public ResultMap<Page<ContractLockup>> getContractLockupList(BlockQueryForm blockQueryForm) {
		String authAddress = blockQueryForm.getAuthAddress();
		String contract = blockQueryForm.getContract();
	//	String address = blockQueryForm.getAddress();
		if(StringUtils.isEmpty(authAddress) || StringUtils.isEmpty(contract))
			return ResultMap.getFailureResult("no authAddress or contract data.");				
		Contract authContract = contractMapper.getAuthorizedContract(contract,authAddress);
		if(authContract==null)
			return ResultMap.getFailureResult("Address does not authorize this contract.");
		boolean isAdmin = authAddress.equalsIgnoreCase(authContract.getAdmin1()) || authAddress.equalsIgnoreCase(authContract.getAdmin2());
		if(!isAdmin){
		//	if(!authAddress.equals(address))
		//		return ResultMap.getFailureResult("This address can not view other address`s record.");
			blockQueryForm.setAddress(authAddress);
		}
		Page<ContractLockup> page = blockQueryForm.newFormPage();
		Long pageSize = page.getSize();
		Long pageCurrent = page.getCurrent();
		pageCurrent = (pageCurrent - 1) * pageSize;
		blockQueryForm.setCurrent(pageCurrent);
				
		List<ContractLockup> list = contractLockupMapper.getPageList(blockQueryForm);
		for(ContractLockup item:list){
			if(item.getType()==1)
				item.setPickupamount(item.getPickamount());
		}
		long total = contractLockupMapper.getTotal(blockQueryForm);
		page.setRecords(list);
		page.setTotal(total);
		return ResultMap.getSuccessfulResult(page);
	}
	
	
	
	
	
	
	
	
	
	
	
    @Autowired
    private ContractverifiedMapper contractverifiedMapper;
    
    public ResultMap<?> getContractAbi(String address) {
        List<Contractverified> listInfo =contractverifiedMapper.getContractInfo(address);
        return ResultMap.getSuccessfulResult(listInfo);
    }

	

}
