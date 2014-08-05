package com.google.code.pathlet.core.instanceroot.user;




public interface UserService {

	User getUser(String username);

	void saveUser(User user);
	
}