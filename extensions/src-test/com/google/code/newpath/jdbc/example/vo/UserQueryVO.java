package com.google.code.newpath.jdbc.example.vo;

import com.google.code.pathlet.vo.QueryParamVo;

public class UserQueryVO extends QueryParamVo {
	
	private String username;
	
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
