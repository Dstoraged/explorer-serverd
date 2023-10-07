package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.TokenQueryParam;
import lombok.Data;

@Data
public class TokenQueryForm   extends BaseQueryForm<TokenQueryParam> {
    private  Integer type ;

    private  String name;

    private  String symbol;
}
