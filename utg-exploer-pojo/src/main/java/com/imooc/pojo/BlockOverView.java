package com.imooc.pojo;


import java.math.BigDecimal;
public class BlockOverView {

    private  BigDecimal totalutg;
    private BigDecimal utgtogb;

    public BigDecimal getTotalutg() {
        return totalutg;
    }

    public void setTotalutg(BigDecimal totalutg) {
        this.totalutg = totalutg;
    }

    public BigDecimal getUtgtogb() {
        return utgtogb;
    }

    public void setUtgtogb(BigDecimal utgtogb) {
        this.utgtogb = utgtogb;
    }

    public String getNetelect() {
        return netelect;
    }

    public void setNetelect(String netelect) {
        this.netelect = netelect;
    }

    private  String  netelect;
    

    public BigDecimal getTotalstorage() {
        return totalstorage;
    }

    public void setTotalstorage(BigDecimal totalstorage) {
        this.totalstorage = totalstorage;
    }

    public BigDecimal getPladgenum() {
        return pladgenum;
    }

    public void setPladgenum(BigDecimal pladgenum) {
        this.pladgenum = pladgenum;
    }

    public BigDecimal getLocknum() {
        return locknum;
    }

    public void setLocknum(BigDecimal locknum) {
        this.locknum = locknum;
    }
    
    /*private double bandwidth;
    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }*/
    private BigDecimal bandwidth;
    public BigDecimal getBandwidth() {
        return bandwidth;
    }
    public void setBandwidth(BigDecimal bandwidth) {
        this.bandwidth = bandwidth;
    }
    
    private BigDecimal totalstorage;
    private BigDecimal pladgenum;
    private BigDecimal locknum;
    private BigDecimal fwpledgenum;

    public BigDecimal getFwpledgenum() {
        return fwpledgenum;
    }

    public void setFwpledgenum(BigDecimal fwpledgenum) {
        this.fwpledgenum = fwpledgenum;
    }

    public BigDecimal getNodepledgenum() {
        return nodepledgenum;
    }

    public void setNodepledgenum(BigDecimal nodepledgenum) {
        this.nodepledgenum = nodepledgenum;
    }

    private BigDecimal nodepledgenum;
}
