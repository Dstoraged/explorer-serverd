package com.imooc.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Contract;
import com.imooc.pojo.ContractLockup;
import com.imooc.pojo.ContractSource;
import com.imooc.pojo.ContractVersion;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.service.ContractService;
import com.imooc.utils.Constants;
import com.imooc.utils.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@CrossOrigin
@RestController
@Api(value = "contract", tags = "contract")
@RequestMapping
public class ContractController {
	
    @Autowired
    private ContractService contractService;
    
    @Value("${remote.server.url:}")
    private String remoteServerUrl;
    @Autowired
    private RestTemplate restTemplate;
    
    @ApiOperation(value = "getContractList")
	@PostMapping("/platform/getContractList")
	public ResultMap<Page<Contract>> getContractList(@Valid @RequestBody BlockQueryForm blockQueryForm) {
    	blockQueryForm.setContract(Constants.pre0XtoNX(blockQueryForm.getContract()));
		return contractService.getContractList(blockQueryForm);
	}

    @ApiOperation(value = "getContractInfo")
	@RequestMapping("/platform/getContractInfo")
	public ResultMap<?> getContractInfo(@RequestParam(value = "contract", required = true) String contract) {
		contract = Constants.pre0XtoNX(contract);
		return contractService.getContractInfo(contract);
	}
    
    @ApiOperation(value = "getContractVersions")
    @RequestMapping("/platform/getContractVersions")
   	public ResultMap<List<ContractVersion>> getContractVersions() {    	
   		return contractService.getContractVersions();
   	}
    
    @ApiOperation(value = "getContractSources")
   	@PostMapping("/platform/getContractSources")
   	public ResultMap<List<ContractSource>> getContractSources(@RequestParam(value = "contract", required = true) String contract) {
    	contract = Constants.pre0XtoNX(contract);
   		return contractService.getContractSources(contract);
   	}
    
    @ApiOperation(value = "verifyContractSources")
    @PostMapping("/platform/verifyContractSources")
	public ResultMap<?> verifyContractSources(@RequestParam(value = "contract", required = true) String contract,
			@RequestParam(value = "name", required = true) String name, 
			@RequestParam(value = "version", required = true) String version, 
			@RequestParam(value = "files", required = true) MultipartFile[] files)  {
    	if(remoteServerUrl!=null && !remoteServerUrl.trim().equals("")){    		
    		MultiValueMap<String, Object> parts =  new LinkedMultiValueMap<>();
    		parts.add("contract", contract);
    		parts.add("name", name);
    		parts.add("version", version);
			for (MultipartFile file : files) {
				File f;
				try {
					f = File.createTempFile("contract-verify-", file.getOriginalFilename());
					file.transferTo(f);
					FileSystemResource fs = new FileSystemResource(f);
					parts.add("files", fs);
				} catch (IOException e) {
					log.warn("Transfer file error", e);
					return ResultMap.getFailureResult(e.getMessage());
				}
			}    		
    		HttpHeaders headers = new HttpHeaders();
    	    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);
    		String url = remoteServerUrl+"/platform/verifyContractSources";
    		log.info("Remote post to "+url+",param:contract"+contract+",name="+name+",version="+version+",files size="+files.length);
    		try{
    		//	ResponseEntity<ResultMap> responseResult = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ResultMap.class);
    			ResponseEntity<String> responseResult = restTemplate.postForEntity(url, requestEntity, String.class);
    			String resultString = responseResult.getBody();
    			log.info("Remote post result is "+resultString+"");  
    			JSONObject resultJson = JSONObject.parseObject(resultString);
    			ResultMap resultMap = new ResultMap(resultJson.getInteger("statusCode"), resultJson.getString("message"), resultJson.get("result"));
    			return resultMap;
    		}catch(Exception e){
    			log.warn("Remote post error",e);
    			return ResultMap.getFailureResult(e.getMessage());
    		}
    	}
		contract = Constants.pre0XtoNX(contract);
		return contractService.verifyContractSources(contract, name, version, files);
	}
    
	@ApiOperation(value = "getLockupContracts")
	@RequestMapping("/platform/getLockupContracts")
	public ResultMap<?> getLockupContracts(@RequestParam(value = "address", required = false) String address,@RequestParam(value = "admin", required = false) String admin) {
		address = Constants.pre0XtoNX(address);
		admin = Constants.pre0XtoNX(admin);		
		return contractService.getLockupContracts(address,admin);
	}
	
	@ApiOperation(value = "getContractLockupList")
	@PostMapping("/platform/getContractLockupList")
	public ResultMap<Page<ContractLockup>> getContractLockupList(@Valid @RequestBody BlockQueryForm blockQueryForm) {
		blockQueryForm.setAddress(Constants.pre0XtoNX(blockQueryForm.getAddress()));
		blockQueryForm.setContract(Constants.pre0XtoNX(blockQueryForm.getContract()));
		blockQueryForm.setAuthAddress(Constants.pre0XtoNX(blockQueryForm.getAuthAddress()));
		return contractService.getContractLockupList(blockQueryForm);
	}


    @ApiOperation(value = "getAbi", notes = "getAbi")
    @RequestMapping("/contract/getAbi")
    public ResultMap<?> getLeatestBlockNumber(@RequestParam(value="address",required=true)String address) throws Exception {
        return contractService.getContractAbi(address);
    }


}
