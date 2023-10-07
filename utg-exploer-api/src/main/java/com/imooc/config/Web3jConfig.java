package com.imooc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

	 @Value("${ipaddress}")
	 private String ipaddress;   //url
	 
	 @Bean
	 public Web3j buildWeb3j(){
		 Web3j web3j = Web3j.build(new HttpService(ipaddress));
		 return web3j;
	 }
}
