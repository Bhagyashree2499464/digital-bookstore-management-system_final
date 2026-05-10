package com.cts.demo.service.impl;

import com.cts.demo.dao.CategoryDao;
import com.cts.demo.exception.CategoryNotFoundException;
import com.cts.demo.model.Category;
import com.cts.demo.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    /**
     * Adds a new category by name.
     *
     * @param categoryName
     * @return the saved category, or null if not found after saving
     */
    @Override
    public Category addCategory(String categoryName) {
        int res = categoryDao.addCategory(categoryName);

        return categoryDao.findByName(categoryName).orElse(null);
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of all categories
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    /**
     * Finds a category by name.
     *
     * @param categoryName
     * @return an Optional containing the category
     */
    @Override
    public Optional<Category> findByName(String categoryName) {
        return categoryDao.findByName(categoryName);
    }

    /**
     * Finds a category by ID.
     *
     * @param categoryId
     * @return an Optional containing the category
     */
    @Override
    public Optional<Category> findById(int categoryId) {
        return Optional.empty();
    }
}

