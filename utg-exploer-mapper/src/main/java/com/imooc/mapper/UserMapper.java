package com.imooc.mapper;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.User;
import com.imooc.utils.MyMapper;

public interface UserMapper extends MyMapper<User> {

  public void addReceiveLikeCount(@Param("userId") String userId);
  
  public void reduceReceiveLikeCount(@Param("userId")  String userId);

  public void addFansCount(@Param("userId")  String userId);
  
  public void addFollersCount(@Param("userId")  String userId);
  
  public void reduceFansCount(@Param("userId")  String userId);
  
  public void reduceFollersCount(@Param("userId")  String userId);
  
}