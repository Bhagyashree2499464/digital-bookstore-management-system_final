package com.cts.demo.dao;

import com.cts.demo.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {

    int addCategory(String category);

    List<Category> getAllCategories();

    Optional<Category> findByName(String categoryName);

    Optional<Category> findById(int categoryId);
}