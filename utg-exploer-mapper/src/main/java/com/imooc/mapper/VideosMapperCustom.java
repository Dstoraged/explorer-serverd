package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;

public interface VideosMapperCustom extends MyMapper<Videos> {
	
	public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,@Param("userId") String userId);
    
	public void addVideoLikeCount(@Param("videoId") String videoId);
	public void reduceVideoLikeCount(@Param("videoId") String videoId);
    
	public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);
	
	public List<VideosVO> queryMyFollowVideos(@Param("userId") String userId);
}