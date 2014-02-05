package com.google.code.pathlet.jdbc.instance.transaction;

import java.io.IOException;

import javax.sql.DataSource;

import com.google.code.newpath.jdbc.example.vo.UserQueryVO;
import com.google.code.newpath.jdbc.example.vo.UserVO;
import com.google.code.pathlet.vo.QueryResultVo;

public interface ConfUserService {
	
	void setDataSource(DataSource dataSource) throws IOException;

	UserVO getUser(String username, String password);

	UserVO getUser(String username);

	QueryResultVo<UserVO> queryUserList(UserQueryVO parameters);

	void saveUser(UserVO user);
	
	void updateUser(UserVO user);
	
	void deleteUsers(String[] usernames);

}