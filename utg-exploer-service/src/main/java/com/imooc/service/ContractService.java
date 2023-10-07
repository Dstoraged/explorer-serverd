package com.imooc.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Contract;
import com.imooc.pojo.ContractLockup;
import com.imooc.pojo.ContractSource;
import com.imooc.pojo.ContractVersion;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.utils.ResultMap;

public interface ContractService {
		
	ResultMap<Page<Contract>> getContractList(BlockQueryForm blockQueryForm);
	
	ResultMap<Contract> getContractInfo(String contract);
	
	ResultMap<List<ContractVersion>> getContractVersions();
	
	ResultMap<List<ContractSource>> getContractSources(String contract);
	
	ResultMap<?> verifyContractSources(String contract,String name,String version,MultipartFile[] files);
	
	ResultMap<?> getLockupContracts(String address,String admin);
	
	ResultMap<Page<ContractLockup>> getContractLockupList(BlockQueryForm blockQueryForm);
	
	
	
    ResultMap<?> getContractAbi(String address);
}
