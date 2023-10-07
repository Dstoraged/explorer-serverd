package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.TransactionQueryParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransactionQueryForm extends BaseQueryForm<TransactionQueryParam> {

    @ApiModelProperty
    private String param;

    @ApiModelProperty
    private Long  blockNumber;

    @ApiModelProperty
    private String  addressHash;

    @ApiModelProperty
    private String txHash;

    @ApiModelProperty
    private Integer type;


    private String appid;

    private String random;

    private String sign;




}
