package com.imooc.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class UtgNetStatics {


    /* yyyy-MM-dd*/
    @Column(name="ctime")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date  ctime;

    /**
     * Exchange traffic value per GB
     */
    @Column(name = "utg_gbrate")
    private BigDecimal utg_gbrate;

    /**
     *
     */
    @Column(name = "total_utg")
    private BigDecimal total_utg;

    /**
     * Total  bandwidth
     */
    @Column(name = "total_bw")
    private BigDecimal total_bw;
    /**
     * Increased bandwidth compared to the previous day
     */
    @Column(name = "incre_bw")
    private BigDecimal incre_bw;


    
    private Long timestamp;

}
