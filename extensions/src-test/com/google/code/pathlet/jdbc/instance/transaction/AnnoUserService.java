package com.google.code.pathlet.jdbc.instance.transaction;

import com.google.code.newpath.jdbc.example.vo.UserVO;

public interface AnnoUserService {
	
	public void deleteUsers(String[] usernames);
	
	public UserVO getUser(String username);
	
	public void saveUser(UserVO user);
	
}