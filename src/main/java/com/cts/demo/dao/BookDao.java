
package com.cts.demo.dao;

import com.cts.demo.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookDao {

    int addBook(Book book);

    int updateBookWithAllProp(Book book);

    int deleteBook(int bookId);

    List<Book> getAllBooks();

    Optional<Book> getBookById(int id);

    List<Book> findByTitle(String title);

    List<Book> findByAuthorName(String authorName);

    List<Book> findByCategory(String category);

    List<Book> findByTitleAndAuthorName(String title, String auhtorName);

    List<Book> findByCategoryAndAuthorName(String category, String auhtorName);

    List<Book> findByCategoryAndTitle(String category, String title);

    List<Book> findByCategoryAndTitleAndAuthorName(String category, String title, String authorName);

    int updateBook(int bookId, int stockQuantity);

}