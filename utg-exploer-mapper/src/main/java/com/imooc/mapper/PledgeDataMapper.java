package com.imooc.mapper;

import com.imooc.pojo.Form.PledgeQueryForm;
import com.imooc.pojo.Form.PledgeRnQueryForm;
import com.imooc.pojo.PledgeData;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PledgeDataMapper extends MyMapper<PledgeData> {

    List<Map<String, Object>> selectAllForAddress(PledgeQueryForm pledgeQueryForm);

    Map<String, Object> selectAllForAddressInfo(@Param("pledgeQueryForm")PledgeQueryForm pledgeQueryForm);

    PledgeData getIsExit(PledgeQueryForm pledgeQueryForm);

    List<Map<String, Object>> getAddressPledgeInfo(PledgeQueryForm pledgeQueryForm);

    void insertOrUpdate(@Param("transaction")PledgeData pledgeData);

    List<PledgeData> selectPledgeDataInfo(@Param("pledgeRnQueryForm")PledgeRnQueryForm pledgeRnQueryForm);
}
