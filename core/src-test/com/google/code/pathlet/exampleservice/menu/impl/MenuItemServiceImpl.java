package com.google.code.pathlet.exampleservice.menu.impl;

import com.google.code.pathlet.exampleservice.menu.MenuItem;
import com.google.code.pathlet.exampleservice.menu.MenuItemService;

public class MenuItemServiceImpl implements MenuItemService {
	

	
	public void saveItem(MenuItem item) {
		System.out.println("saving menu item id=" + item.getMenuId() + ", name=" + item.getName());
	}
	
	public MenuItem getItem(String itemId) {
		return new MenuItem("11", "1", new Integer(2), "menuItem1", "http:www.sina.com.cn");
	}
	
	

}
