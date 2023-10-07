package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.TransferMinerQueryParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransferMinerForm  /*extends BaseQueryForm*/<TransferMinerQueryParam> {

    private Integer[] type;

    private Long id;

}
