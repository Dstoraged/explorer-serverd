package com.imooc.mapper;

import com.imooc.pojo.Form.PledgeQueryForm;
import com.imooc.pojo.PledgeTotalData;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PledgeTotalDataMapper extends MyMapper<PledgeTotalData> {
    Map<String, Object> getTotalForAddress(@Param("address")String address,@Param("type") Integer type);

    Map<String, Object> selectAllForAddressInfo(PledgeQueryForm pledgeQueryForm);

    List<Map<String, Object>> getForPledgeInfo(PledgeQueryForm pledgeQueryForm);

    void savePledgeTotalData(@Param("pledgetotaldata")PledgeTotalData info);

    void updateTotalData(@Param("address")String address,@Param("nodeNum") String nodeNum, @Param("values")BigDecimal values, @Param("date") Date date,@Param("type") Integer type,@Param("blockNumber")Long blockNumber);

    void updateTotalDataForExit(@Param("address")String address,@Param("blockNumber")Long blockNumber, @Param("punilsh")BigDecimal punilsh, @Param("releaseHeight")Long releaseHeight, @Param("releaseInterval")Long releaseInterval,@Param("nodeNum") String nodeNum,@Param("values")BigDecimal values,@Param("date") Date date, @Param("type")Integer type,@Param("resultType")Integer resultType,@Param("status")Integer status);

    void updateTotalDataForCashOver(@Param("date") Date date,@Param("blockNumber")Long blockNumber,@Param("address")String address,@Param("paramType") Integer paramType, @Param("type")Integer type,@Param("cashAmount")BigDecimal cashAmount,@Param("nodeNum") String nodeNum);

    List<PledgeTotalData> selectPageList(@Param("pledgeQueryForm")PledgeQueryForm pledgeQueryForm);

    long seleTotalCount(@Param("pledgeQueryForm")PledgeQueryForm pledgeQueryForm);

    PledgeTotalData selectPunilshAmount(@Param("address")String address, @Param("type")Integer type);

    List<PledgeTotalData> selectPledgeTotalDataInfo(@Param("pledgeQueryForm")PledgeQueryForm pledgeQueryForm);
}
