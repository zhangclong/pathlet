package com.google.code.pathlet.core.instanceroot.user.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.code.pathlet.core.instanceroot.user.User;
import com.google.code.pathlet.core.instanceroot.user.UserService;

public class UserServiceImpl implements UserService {
	
	private Map<String, User> data = new HashMap<String, User>();

	public User getUser(String username) {
		return data.get(username);
	}

	public void saveUser(User user) {
		data.put(user.getUsername(), user);
	}

}
