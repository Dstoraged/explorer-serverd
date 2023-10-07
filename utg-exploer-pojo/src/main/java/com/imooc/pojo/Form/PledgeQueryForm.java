package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.TransferMinerQueryParam;
import lombok.Data;

@Data
public class PledgeQueryForm extends BaseQueryForm<TransferMinerQueryParam> {
    private Integer type;

    private String  address;

    private String nodeNum;

    private Integer status;

    private Long blockNumber;

}
