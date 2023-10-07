package com.imooc.service;

import com.imooc.pojo.User;
import com.imooc.pojo.UsersReport;


public interface UserService {
	public boolean queryUsernameIsExist(String username);

	public void saveUser(User user);
	public User queryUserForLogin(String username,String password);
 
    public void updateUserInfo(User user);
    
    public User queryUserInfo(String userId);
    
    public boolean isUserLikeVideo(String userId,String videoId);
    
    public void saveUserFanRelation(String userId,String fanId);
    
    public void deleteUserFanRelation(String userId,String fanId);
    
    public boolean queryIfFollow(String userId,String fanId);

    public void reportUser(UsersReport usersReport);
}
