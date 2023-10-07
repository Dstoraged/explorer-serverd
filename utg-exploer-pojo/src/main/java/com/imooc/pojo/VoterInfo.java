package com.imooc.pojo;

import lombok.Data;

import java.math.BigInteger;
@Data
public class VoterInfo {
    private String Candidate;
    private String Voter;
    private BigInteger Stake;
    private BigInteger BlockNumber;//

}
