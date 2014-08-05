package com.google.code.pathlet.core.instanceroot.common.impl;

import com.google.code.pathlet.config.anno.InstanceIn;
import com.google.code.pathlet.core.instanceroot.common.DataInitService;
import com.google.code.pathlet.core.instanceroot.menu.Category;
import com.google.code.pathlet.core.instanceroot.menu.CategoryService;
import com.google.code.pathlet.core.instanceroot.user.User;
import com.google.code.pathlet.core.instanceroot.user.UserService;

public class DataInitServiceImpl implements DataInitService {

	@InstanceIn(path = "../user/UserService")
	private UserService userService;
	
	@InstanceIn(path = "../menu/CategoryService")
	private CategoryService categoryService;
	
	
	public UserService getUserService() {
		return userService;
	}

	public void saveInit() {
	
		categoryService.save(new Category("1", "agent", "Agent Users Category"));
		categoryService.save(new Category("2", "client", "Clients Users Category"));
		categoryService.save(new Category("3", "provider", "Provider Users Category"));
		
		userService.saveUser(new User("admin", "Administrator", "admin@gmail.com"));
		userService.saveUser(new User("charlie", "Charlie Zhang", "zhangclong@gmail.com"));
		userService.saveUser(new User("anastasia", "Anastasia Kong", "kong@gmail.com"));
	}

}
