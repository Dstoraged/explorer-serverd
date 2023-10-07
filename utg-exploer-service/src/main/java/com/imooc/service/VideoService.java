package com.imooc.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;


public interface VideoService {
 
	public String saveVideo(Videos video);
	
	public void updateVideo(String videoId,String coverPath);
	
	public PagedResult getAllVides(Videos video, Integer isSaveRecord,Integer page,Integer pageSize);
	
    public List<String> getHotwords();
    
    public void userLikeVideo(String userId,String videoId,String videoCreaterId);
   
    public void userUnLikeVideo(String userId,String videoId,String videoCreaterId);
    
    public PagedResult queryMyLikeVideos(String userId,Integer page,Integer pageSize);

    public PagedResult queryMyFollowVideos(String userId,Integer page,Integer pageSize);
     
    public void saveComment(Comments comment);
    
    public PagedResult getAllComments(String videoId,Integer page,Integer pageSize);
}
