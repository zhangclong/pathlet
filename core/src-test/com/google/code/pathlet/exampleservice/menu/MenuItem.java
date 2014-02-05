package com.google.code.pathlet.exampleservice.menu;


public class MenuItem  implements java.io.Serializable {

	private static final long serialVersionUID = 7385907497605353879L;
	
	public static final String ROOT_MENU_ID = "1";
	
	public static final String PATH_SEPARATOR = "/";
	
	public static final String PATH_TYPE_THIS = "this";
	
	public static final String PATH_TYPE_ALL = "all";
	
	public static final int TYPE_FOLDER = 2;
	
	public static final int TYPE_FILE = 1;
	
	private String menuId;
	private String parentMenuId;
	private Integer type;
	private String name;
	private String link;
	private String notes;

	public MenuItem(String menuId, String parentMenuId, Integer type, String name, String link) {
		this.menuId = menuId;
		this.parentMenuId = parentMenuId;
		this.type = type;
		this.name = name;
		this.link = link;
	}
	
    public MenuItem() {
    }

    public String getMenuId() {
        return this.menuId;
    }
    
    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
    public String getParentMenuId() {
        return this.parentMenuId;
    }
    
    public void setParentMenuId(String parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
        //return "New..." + this.name;
		return this.name;
    }
	
	
	
    
    public void setName(String name) {
        this.name = name;
    }
    public String getLink() {
        return this.link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }

    public String getNotes() {
        return this.notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((menuId == null) ? 0 : menuId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuItem other = (MenuItem) obj;
		if (menuId == null) {
			if (other.menuId != null)
				return false;
		} else if (!menuId.equals(other.menuId))
			return false;
		return true;
	}

}


