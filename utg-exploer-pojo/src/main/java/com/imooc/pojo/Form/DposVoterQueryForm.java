package com.imooc.pojo.Form;


import lombok.Data;



@Data
public class DposVoterQueryForm  {

    private String startTime ;

    private String endTime;

    private Integer round;

    private String voterAddress;

    private String candidateAddress;

    private Long stratBlockNumber;

    private Long endBlockNumber;
    

}
