package com.cts.demo.dao;

import com.cts.demo.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    int addAuthor(String authorName);
    Optional<Author> findByAuthorId(int authorId);
    Optional<Author> findByAuthorName(String name);
    List<Author> findAll();
}
