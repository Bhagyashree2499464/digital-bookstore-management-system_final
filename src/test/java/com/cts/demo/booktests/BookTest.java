package com.cts.demo.booktests;

import com.cts.demo.dao.AuthorDao;
import com.cts.demo.dao.BookDao;
import com.cts.demo.dao.CategoryDao;
import com.cts.demo.exception.AuthorNotFoundException;
import com.cts.demo.exception.BookAlreadyExistsException;
import com.cts.demo.exception.CategoryNotFoundException;
import com.cts.demo.model.Author;
import com.cts.demo.model.Book;
import com.cts.demo.model.Category;
import com.cts.demo.service.impl.BookServiceImpl;
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
 * Unit tests for {@link BookServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class BookTest {

    @Mock
    private BookDao bookDao;

    @Mock
    private AuthorDao authorDao;

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private Author author;
    private Category category;

    /**
     * Sets up sample book, author and category before each test.
     */
    @BeforeEach
    void setUp() {
        author = new Author(1, "John");
        category = new Category(1, "Fiction");

        book = new Book(
                100,
                "Java Basics",
                500.0,
                20,
                author.getAuthorID(),
                category.getCategoryId()
        );
    }

    /**
     * Tests that addBook throws BookAlreadyExistsException when book already exists.
     */
    @Test
    void addBook_shouldThrowException_whenBookAlreadyExists() {

        when(authorDao.findByAuthorId(author.getAuthorID()))
                .thenReturn(Optional.of(author));

        when(bookDao.findByTitleAndAuthorName("Java Basics", "John"))
                .thenReturn(List.of(book));

        assertThrows(
                BookAlreadyExistsException.class,
                () -> bookService.addBook(book)
        );

        verify(bookDao, never()).addBook(book);
    }

    /**
     * Tests that addBook calls DAO when book does not already exist.
     */
    @Test
    void addBook_shouldCallDao_whenBookDoesNotExist() {

        when(authorDao.findByAuthorId(author.getAuthorID()))
                .thenReturn(Optional.of(author));

        when(bookDao.findByTitleAndAuthorName("Java Basics", "John"))
                .thenReturn(List.of());

        bookService.addBook(book);

        verify(bookDao).addBook(book);
    }

    /**
     * Tests that deleteBook returns number of rows deleted.
     */
    @Test
    void deleteBook_shouldReturnRowsDeleted() {
        when(bookDao.deleteBook(100)).thenReturn(1);
        assertEquals(1, bookService.deleteBook(100));
    }

    /**
     * Tests that getAllBooks returns list of books.
     */
    @Test
    void getAllBooks_shouldReturnList() {
        when(bookDao.getAllBooks()).thenReturn(List.of(book));
        assertEquals(1, bookService.getAllBooks().size());
    }

    /**
     * Tests that getBookById returns the book when ID exists.
     */
    @Test
    void getBookById_shouldReturnBook() {
        when(bookDao.getBookById(100)).thenReturn(Optional.of(book));
        assertNotNull(bookService.getBookById(100));
    }

    /**
     * Tests that getBooksByTitle returns list of matching books.
     */
    @Test
    void getBooksByTitle_shouldReturnList() {
        when(bookDao.findByTitle("Java Basics")).thenReturn(List.of(book));
        assertFalse(bookService.getBooksByTitle("Java Basics").isEmpty());
    }

    /**
     * Tests that getBooksByTitleAndAuthorName returns list when author is valid.
     */
    @Test
    void getBooksByTitleAndAuthorName_shouldReturnList_whenAuthorValid() {
        when(authorDao.findByAuthorName("John")).thenReturn(Optional.of(author));
        when(bookDao.findByTitleAndAuthorName("Java Basics", "John"))
                .thenReturn(List.of(book));

        assertEquals(
                1,
                bookService.getBooksByTitleAndAuthorName("Java Basics", "John").size()
        );
    }

    /**
     * Tests that getBooksByTitleAndAuthorName throws AuthorNotFoundException when author is invalid.
     */
    @Test
    void getBooksByTitleAndAuthorName_shouldThrowException_whenAuthorInvalid() {
        when(authorDao.findByAuthorName("Invalid")).thenReturn(Optional.empty());

        assertThrows(
                AuthorNotFoundException.class,
                () -> bookService.getBooksByTitleAndAuthorName("Java", "Invalid")
        );
    }

    /**
     * Tests that getByCategory returns list when category is valid.
     */
    @Test
    void getByCategory_shouldReturnList_whenCategoryValid() {
        when(categoryDao.findByName("Fiction")).thenReturn(Optional.of(category));
        when(bookDao.findByCategory("Fiction")).thenReturn(List.of(book));

        assertEquals(1, bookService.getByCategory("Fiction").size());
    }

    /**
     * Tests that getByCategory throws CategoryNotFoundException when category is invalid.
     */
    @Test
    void getByCategory_shouldThrowException_whenCategoryInvalid() {
        when(categoryDao.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> bookService.getByCategory("Unknown")
        );
    }

    /**
     * Tests that updateBookWithAllProp returns number of rows updated.
     */
    @Test
    void updateBookWithAllProp_shouldReturnUpdatedRows() {
        when(bookDao.updateBookWithAllProp(book)).thenReturn(1);
        assertEquals(1, bookService.updateBookWithAllProp(book));
    }
}