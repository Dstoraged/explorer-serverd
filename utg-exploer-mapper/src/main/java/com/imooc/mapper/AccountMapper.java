package com.imooc.mapper;

import com.imooc.pojo.Addresses;
import com.imooc.pojo.Form.BlockQueryForm;
import com.imooc.pojo.vo.AddressVo;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AccountMapper extends MyMapper<Addresses> {
    List<Addresses> selectSingleAddress(@Param("address")String address);

    List<String> getMultipleByHash(@Param("hashParams")String[] hashParams);

    List<AddressVo> getPageList(@Param("pageCurrent")Long pageCurrent, @Param("pageSize")Long pageSize,@Param("address")String address);

    long getTotalAddress(@Param("addressHash")String addressHash);

    int saveOrUpdata(@Param("accounts")Addresses item);

    List<Addresses> getAllAddressFoBalance(@Param("paramMap")Map<String, Object> map);

    long getTotal(@Param("address")String address);

    Long getNewAddress(@Param("param")Map<String, Object> paramMap);

    long getContractCount(@Param("contractAddress") String contractAddress);

    List<AddressVo> getContractPageList(@Param("blockQueryForm") BlockQueryForm blockQueryForm);

    int saveOrUpdataToken(@Param("accounts")Addresses item);

    List<AddressVo> getContractPageTokenList(@Param("blockQueryForm")BlockQueryForm blockQueryForm);

    long getContractTokenCount(@Param("contractAddress")String contractAddress);

    void updateHaslock(@Param("address")String address);

    void saveOrUpdataHaslock(@Param("accounts")Addresses item);

    Long getLeasetSrtBlock();
    
    int saveOrUpdateSrt(@Param("accounts")Addresses accounts);
    
    void updateSrt(@Param("accounts")Addresses item);

    List<AddressVo> getAllAddress();

    AddressVo  getAddressVoByaddress(@Param("address")String address);

    Addresses  getAddressInfo(@Param("address")String address);
    
    int setAsStorage(@Param("address")String address,@Param("blockNumber")Long blockNumber);
    int setAsRevenue(@Param("address")String address,@Param("blockNumber")Long blockNumber);
    int setAsMiner(@Param("address")String address,@Param("blockNumber")Long blockNumber);
    int setAsContract(@Param("address")String address,@Param("blockNumber")Long blockNumber); 
}
