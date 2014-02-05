package com.google.code.pathlet.exampleservice.user;




public interface UserService {

	User getUser(String username);

	void saveUser(User user);
	
}