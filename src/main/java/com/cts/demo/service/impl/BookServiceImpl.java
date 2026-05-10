package com.cts.demo.service.impl;

import com.cts.demo.DigitalBookstoreManagementSystemApplication;
import com.cts.demo.exception.*;
import com.cts.demo.dao.AuthorDao;
import com.cts.demo.dao.BookDao;
import com.cts.demo.dao.CategoryDao;
import com.cts.demo.model.Author;
import com.cts.demo.model.Book;
import com.cts.demo.model.Category;
import com.cts.demo.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;

    private final AuthorDao authorDao;

    private final CategoryDao categoryDao;

    private static final Logger log =
            LoggerFactory.getLogger(DigitalBookstoreManagementSystemApplication.class);

    /**
     * Constructs BookServiceImpl with required DAOs.
     * @param bookDao
     * @param authorDao
     * @param categoryDao
     */
    public BookServiceImpl(BookDao bookDao, AuthorDao authorDao, CategoryDao categoryDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.categoryDao = categoryDao;
    }

    /**
     * Validates that an author exists by name.
     * @param authorName
     * @return
     */
    private Author isValidAuthor(String authorName){
        log.info("Validating author: {}", authorName);
        return authorDao.findByAuthorName(authorName)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found"));
    }

    /**
     * Validates that a category exists by name.
     * @param categoryName
     * @return
     */
    private Category isValidCategory(String categoryName){

        log.info("Validating category: {}", categoryName);
        return categoryDao.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }

    /**
     * Retrieves a book by its ID.
     * @param id
     * @return
     */
    @Override
    public Book getBookById(int id){
        log.info("Fetching book with ID: {}", id);
        return bookDao.getBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

    }

    /**
     * Adds a new book after validating author and checking for duplicates.
     * @param book
     */
    @Override
    public void addBook(Book book) {

        log.info("Adding new book with title: {}", book.getTitle());

        Author author = authorDao.findByAuthorId(book.getAuthorId())
                .orElseThrow(() ->
                        new AuthorNotFoundException(
                                "Author not found with id: " + book.getAuthorId()
                        ));

        boolean exists = !bookDao.findByTitleAndAuthorName(
                book.getTitle(),
                author.getAuthorName()
        ).isEmpty();

        if (exists) {
            throw new BookAlreadyExistsException("Book already exists");
        }

        bookDao.addBook(book);
        log.info("Book added successfully: {}", book.getTitle());
    }

    /**
     * Deletes a book by its ID.
     * @param bookId
     * @return
     */
    @Override
    public int deleteBook(int bookId) {

        log.info("Deleting book with ID: {}", bookId);
        int rows = bookDao.deleteBook(bookId);
        if (rows == 0) {
            throw new BookNotFoundException("Book not found with ID: " + bookId);
        }

        log.info("Book deleted successfully with ID: {}", bookId);
        return rows;
    }

    /**
     * Retrieves all books.
     * @return
     */
    @Override
    public List<Book> getAllBooks() {
        log.info("Fetching all books");
        return bookDao.getAllBooks();
    }

    /**
     * Finds books by title.
     * @param title
     * @return
     */
    @Override
    public List<Book> getBooksByTitle(String title) {

        log.info("Searching books by title: {}", title);
        return bookDao.findByTitle(title);
    }

    /**
     * Finds books by title and author name.
     * @param title
     * @param authorName
     * @return
     */
    @Override
    public List<Book> getBooksByTitleAndAuthorName(String title, String authorName) {

        log.info("Searching books by title '{}' and author '{}'", title, authorName);
        if(isValidAuthor(authorName) != null){
            return bookDao.findByTitleAndAuthorName(title,authorName);
        }
        return List.of();
    }

    /**
     * Finds books by author name.
     * @param authorName
     * @return
     */
    @Override
    public List<Book> getByAuthorName(String authorName) {

        log.info("Searching books by author: {}", authorName);

        if(isValidAuthor(authorName) != null) {
            return bookDao.findByAuthorName(authorName);
        }
        return List.of();
    }

    /**
     * Finds books by category.
     * @param category
     * @return
     */
    @Override
    public List<Book> getByCategory(String category) {

        log.info("Searching books by category: {}", category);

        isValidCategory(category);
        return bookDao.findByCategory(category);
    }

    /**
     * Finds books by category and author name.
     * @param category
     * @param authorName
     * @return
     */
    @Override
    public List<Book> findByCategoryAndAuthorName(String category, String authorName) {
        log.info("Searching books by category '{}' and author '{}'", category, authorName);
        isValidAuthor(authorName);
        isValidCategory(category);
        return bookDao.findByCategoryAndAuthorName(category, authorName);
    }

    /**
     * Finds books by category and title.
     * @param category
     * @param title
     * @return
     */
    @Override
    public List<Book> findByCategoryAndTitle(String category, String title) {
        log.info("Searching books by category '{}' and title '{}'", category, title);
        isValidCategory(category);
        return bookDao.findByCategoryAndTitle(category, title);
    }

    /**
     * Finds books by category, title, and author name.
     * @param category
     * @param title
     * @param authorName
     * @return
     */
    @Override
    public List<Book> findByCategoryAndTitleAndAuthorName(String category, String title, String authorName) {

        log.info(
                "Searching books by category '{}', title '{}', and author '{}'",
                category, title, authorName
        );

        isValidAuthor(authorName);
        isValidCategory(category);
        return bookDao.findByCategoryAndTitleAndAuthorName(category, title, authorName);

    }

    /**
     * Updates all properties of an existing book.
     * @param book
     * @return
     */
    @Override
    public int updateBookWithAllProp(Book book) {

        log.info("Updating book with ID: {}", book.getBookId());

        if (book.getPrice() <= 0) {
            throw new InvalidBookDataException("Book price must be positive");
        }
        return bookDao.updateBookWithAllProp(book);
    }
}

