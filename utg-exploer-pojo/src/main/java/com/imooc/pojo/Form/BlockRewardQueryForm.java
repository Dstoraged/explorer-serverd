package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.BlockRewardParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BlockRewardQueryForm  extends BaseQueryForm <BlockRewardParam>{

    @ApiModelProperty
    private String address;  //
}
