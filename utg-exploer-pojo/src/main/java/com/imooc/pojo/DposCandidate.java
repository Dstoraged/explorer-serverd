package com.imooc.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DposCandidate {

    private  Long id;

    private Date loopStartTime;

    private String candiDate;

    private BigDecimal tally;

    private Integer state;




}
