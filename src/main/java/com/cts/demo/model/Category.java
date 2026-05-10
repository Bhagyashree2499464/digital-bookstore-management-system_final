package com.cts.demo.model;

import lombok.Data;

@Data
public class Category {

    private int categoryId;
    private String categoryName;

    // No-argument constructor
    public Category() {
    }

    // Parameterized constructor
    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

}