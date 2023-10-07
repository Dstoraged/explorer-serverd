package com.imooc.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imooc.form.BasePo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhc 2021-09-15 14:41
 */
@Data
public class UtgStorageMinerDayVo extends BasePo {


    private String miner_addr;

    private String revenue_address;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date ctime;

    private BigDecimal bandwidth;

    private Long minerstorage;

    private BigDecimal srtnum;

    private BigDecimal profitamount;

    //0 traffic revenue ,1 bandwidth reward
    private  Integer fwflag;

}
