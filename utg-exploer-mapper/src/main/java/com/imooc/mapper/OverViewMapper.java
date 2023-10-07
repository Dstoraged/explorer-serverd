package com.imooc.mapper;

import com.imooc.pojo.BlockOverView;

import java.math.BigDecimal;

public interface OverViewMapper {

    BlockOverView getFwMinerData();
    BlockOverView getNodeMinerData();
    BlockOverView getCurrLockData();
    BlockOverView getutgtogbData();
    BlockOverView getutg24(long blocknumber);
    BigDecimal getBwPdgRnge300();
    BigDecimal getBwPdgRnge380();
    BigDecimal getBwPdgRnge815();
    BigDecimal getBwPdgRnge1500();
    BlockOverView getStorageData();    
}
