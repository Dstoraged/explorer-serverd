package com.imooc.pojo.Form;

import com.imooc.form.BaseQueryForm;
import com.imooc.pojo.Param.BlockQueryParam;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * @author zhc 2021-09-15 15:14
 */
@Data
public class UtgStorageMinerQueryForm extends BaseQueryForm<BlockQueryParam> {

    private String hash;
    private String miner_addr;

    private String revenue_address;

    private String pay_address;

    private Long blocknumber;

    private BigDecimal bandwidth;


    private String time;
    private Boolean isLike;

    private Boolean timeEqual;

    private String sortBy;

    boolean descending;

    private String sortSql;

    private String appid;

    private String random;

    private String sign;
}
