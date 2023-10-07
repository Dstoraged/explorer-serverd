package com.imooc.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.StorageReleaseCompare;
import com.imooc.pojo.StorageReleaseDetail;
import com.imooc.utils.MyMapper;

public interface StorageReleaseMapper  extends MyMapper<StorageReleaseDetail> {

	void insertDetailBatch(@Param("list")List<StorageReleaseDetail> list);
	
	void insertCompareBatch(@Param("list")List<StorageReleaseCompare> list);
    
    List<Map> getRangeReleasePays(@Param("startblock")long startblock, @Param("endblock") long endblock);
    
    List<Map> getRangeReleaseStat(@Param("startblock")long startblock, @Param("endblock") long endblock);
    
}
