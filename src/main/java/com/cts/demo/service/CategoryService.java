package com.cts.demo.service;

import com.cts.demo.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category addCategory(String  categoryName);

    List<Category> getAllCategories();

    Optional<Category> findByName(String categoryName);

    Optional<Category> findById(int categoryId);
}