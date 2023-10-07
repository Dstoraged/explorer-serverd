package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.PledgeEnQueryParam;
import lombok.Data;

@Data
public class PledgeRnQueryForm extends BaseQueryForm<PledgeEnQueryParam> {
    private Integer type;

    private String  address;

    private String beginTime;

    private String endTime;

    private Long blockNumber;

    private String txHash;
}
