package com.imooc.mapper;

import com.imooc.pojo.ApiConfigView;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiConfigMapper {
    List<ApiConfigView> getAllConfig(@Param("types")long types);

    List<ApiConfigView> getAllConfigForEn(@Param("types")long types);
}
