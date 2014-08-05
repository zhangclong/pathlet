package com.google.code.pathlet.core.instanceroot.menu;


public class Category  implements java.io.Serializable {

	private static final long serialVersionUID = -1L;
		
	private String categoryId;
	private String name;
	private String desc;
	
	public Category(String categoryId, String name, String desc) {
		super();
		this.categoryId = categoryId;
		this.name = name;
		this.desc = desc;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	

	

}


