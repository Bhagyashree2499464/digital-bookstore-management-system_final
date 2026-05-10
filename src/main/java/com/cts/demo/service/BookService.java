package com.cts.demo.service;

import com.cts.demo.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {

    void addBook(Book book);

    int updateBookWithAllProp(Book book);

    int deleteBook(int bookId);

    List<Book> getAllBooks();

    Book getBookById(int id);

    List<Book> getBooksByTitle(String title);

    List<Book> getByAuthorName(String authorName);

    List<Book> getByCategory(String category);

    List<Book> getBooksByTitleAndAuthorName(String title, String auhtorName) ;

    List<Book> findByCategoryAndAuthorName(String category, String auhtorName);

    List<Book> findByCategoryAndTitle(String category, String title);

    List<Book> findByCategoryAndTitleAndAuthorName(String category, String title, String authorName);


}