package com.google.code.pathlet.core.instanceroot.menu;

import java.util.List;



public interface MenuItemService {

    MenuItem getItem(String itemId);

    void saveItem(MenuItem item);

}
