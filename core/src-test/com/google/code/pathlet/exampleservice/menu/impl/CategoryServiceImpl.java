package com.google.code.pathlet.exampleservice.menu.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.code.pathlet.exampleservice.menu.Category;
import com.google.code.pathlet.exampleservice.menu.CategoryService;

public class CategoryServiceImpl implements CategoryService {
	
	private Map<String, Category> data = new HashMap<String, Category>();
	
	public void save(Category category) {
		data.put(category.getCategoryId(), category);
	}
	
	public Category get(String categoryId) {
		return data.get(categoryId);
	}

}
