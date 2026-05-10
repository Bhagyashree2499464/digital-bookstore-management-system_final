package com.cts.demo.service.impl;

import com.cts.demo.dao.AuthorDao;
import com.cts.demo.exception.AuthorNotFoundException;
import com.cts.demo.model.Author;
import com.cts.demo.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;
    public AuthorServiceImpl(AuthorDao authorDao){
        this.authorDao = authorDao;
    }

    /**
     * Adds a new author
     *
     * @param authorName
     * @return the saved author
     */
    @Override
    public Author addAuthor(String authorName) {
        authorDao.addAuthor(authorName);
        return authorDao.findByAuthorName(authorName)
            .orElseThrow(() ->
                new AuthorNotFoundException(
                        "Author not found with name: " + authorName
                ));
    }

    /**
     * Finds an author by their ID.
     *
     * @param authorId
     * @return an Optional containing the author
     */
    @Override
    public Optional<Author> findByAuthorId(int authorId) {
        return Optional.ofNullable(authorDao.findByAuthorId(authorId)
                .orElseThrow(() ->
                        new AuthorNotFoundException(
                                "Author not found with id: " + authorId
                        )));
    }

    /**
     * Finds an author by their name.
     *
     * @param name
     * @return an Optional containing the author
     */
    @Override
    public Optional<Author> findByAuthorName(String name) {
        return Optional.ofNullable(authorDao.findByAuthorName(name)
                .orElseThrow(() ->
                        new AuthorNotFoundException(
                                "Author not found with name: " + name
                        )));
    }


    /**
     * Retrieves all authors from the database.
     *
     * @return a list of all authors
     */
    @Override
    public List<Author> findAll() {
        List<Author> authors = authorDao.findAll();
        if(authors.isEmpty()) {
            throw new AuthorNotFoundException("Authors do not exist.");
        }
        return authors;
    }
}
