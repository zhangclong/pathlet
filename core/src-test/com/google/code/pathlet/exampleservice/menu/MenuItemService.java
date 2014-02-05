package com.google.code.pathlet.exampleservice.menu;

import java.util.List;



public interface MenuItemService {

    MenuItem getItem(String itemId);

    void saveItem(MenuItem item);

}
