package com.imooc.pojo.Form;

import com.imooc.form.BaseParam;
import com.imooc.form.BaseQueryForm;

import lombok.Data;

@Data
public class StoragePoolForm  extends BaseQueryForm<BaseParam>{
	private String hash;
	private String managerAddr;
	private String revenueAddr;
	private Integer status;
	private String query;
	private Integer [] statusList;
}
