package com.cts.demo.categorytests;

import com.cts.demo.dao.CategoryDao;
import com.cts.demo.model.Category;
import com.cts.demo.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CategoryServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class CategoryTest {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    /**
     * Sets up a sample category before each test.
     */
    @BeforeEach
    void setUp() {
        category = new Category(1, "Fiction");
    }

    /**
     * Tests that addCategory returns the category when found after insert.
     */
    @Test
    void addCategory_shouldReturnCategory() {
        when(categoryDao.addCategory("Fiction")).thenReturn(1);
        when(categoryDao.findByName("Fiction")).thenReturn(Optional.of(category));

        Category result = categoryService.addCategory("Fiction");

        assertNotNull(result);
    }

    /**
     * Tests that addCategory returns null when category not found after insert.
     */
    @Test
    void addCategory_shouldReturnNull_whenCategoryNotFound() {
        when(categoryDao.addCategory("Unknown")).thenReturn(1);
        when(categoryDao.findByName("Unknown")).thenReturn(Optional.empty());

        Category result = categoryService.addCategory("Unknown");

        assertNull(result);
    }

    /**
     * Tests that getAllCategories returns list of categories.
     */
    @Test
    void getAllCategories_shouldReturnList() {
        when(categoryDao.getAllCategories()).thenReturn(List.of(category));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
    }

    /**
     * Tests that findByName returns the category when name exists.
     */
    @Test
    void findByName_shouldReturnCategory() {
        when(categoryDao.findByName("Fiction")).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.findByName("Fiction");

        assertTrue(result.isPresent());
    }

    /**
     * Tests that findById returns empty Optional.
     */
    @Test
    void findById_shouldReturnEmpty() {
        Optional<Category> result = categoryService.findById(1);

        assertTrue(result.isEmpty());
    }
}