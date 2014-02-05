package com.google.code.pathlet.exampleservice.menu;




public interface CategoryService {

    Category get(String categoryId);

    void save(Category category);

}
