package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.BlockQueryParam;
import lombok.Data;

@Data
public class TxQueryForm extends BaseQueryForm<BlockQueryParam> {
    private Long blocknumber;

    private String txType;

    private String charge_address;

    private String pay_address;

    private String appid;

    private String random;

    private String sign;
}
