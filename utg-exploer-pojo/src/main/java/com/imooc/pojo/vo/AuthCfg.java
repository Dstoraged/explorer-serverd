package com.imooc.pojo.vo;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author zhc 2021-10-12 11:20
 */
@Data
public class AuthCfg {
    private Integer plathid;

    /**
     * Pool name
     */
    private String pool_name;

    /**
     * Pool description
     */
    private String pool_desc;

    /**
     * Random 6 digits
     */
    private String appid;

    /**
     * UUID
     */
    private String appkey;

    /**
     *
     */
    private String allowip;
    /**
     *1 enable, 0 not enable
     */
    private Integer pool_enable;
    /**
     *instime
     */
    private Date instime;
    /**
     *Last Modified
     */
    private Date datetime;

}
