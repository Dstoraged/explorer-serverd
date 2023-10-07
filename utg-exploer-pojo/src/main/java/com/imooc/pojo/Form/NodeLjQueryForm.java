package com.imooc.pojo.Form;

import com.imooc.form.BaseParam;
import com.imooc.form.BaseQueryForm;

import lombok.Data;

@Data
public class NodeLjQueryForm   extends BaseQueryForm<BaseParam>{

	private String targetAddress;
	private String nodeAddress;
	private String revenueAddress;
	private String nodeType;
	private Integer rewardType; 
	private Long  blockNumber;
}
