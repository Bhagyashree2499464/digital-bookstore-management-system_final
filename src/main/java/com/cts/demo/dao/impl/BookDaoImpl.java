package com.cts.demo.dao.impl;

import com.cts.demo.dao.BookDao;
import com.cts.demo.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of {@link BookDao}.
 */
@Repository
public class BookDaoImpl implements BookDao {

    private static final String INSERT_SQL = """
            INSERT INTO BOOK (TITLE, PRICE, STOCKQUANTITY, AUTHORID, CATEGORYID)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String DELETE_SQL = "DELETE FROM BOOK WHERE BOOKID=?";

    private static final String UPDATE_ALL_SQL = """
            UPDATE BOOK SET TITLE=?,
            PRICE=?,
            STOCKQUANTITY=?,
            AUTHORID=?,
            CATEGORYID=?
            WHERE BOOKID=?
            """;

    private static final String UPDATE_STOCK_SQL = """
            UPDATE BOOK SET STOCKQUANTITY=?
            WHERE BOOKID=?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT BOOKID, TITLE, PRICE, STOCKQUANTITY, AUTHORID, CATEGORYID
            FROM BOOK
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT BOOKID, TITLE, PRICE, STOCKQUANTITY, AUTHORID, CATEGORYID
            FROM BOOK
            WHERE BOOKID = ?
            """;

    private static final String FIND_BY_TITLE_SQL = """
            SELECT BOOKID, TITLE, PRICE, STOCKQUANTITY, AUTHORID, CATEGORYID
            FROM BOOK
            WHERE TITLE = ?
            """;

    private static final String FIND_BY_AUTHOR_SQL = """
            SELECT B.BOOKID, B.TITLE, B.PRICE, B.STOCKQUANTITY, B.AUTHORID, B.CATEGORYID
            FROM BOOK B
            JOIN AUTHOR A ON B.AUTHORID = A.AUTHORID
            WHERE A.AUTHORNAME = ?
            """;

    private static final String FIND_BY_CATEGORY_SQL = """
            SELECT *
            FROM BOOK B
            JOIN CATEGORY C ON B.CATEGORYID = C.CATEGORYID
            WHERE C.CATEGORYNAME = ?
            """;

    private static final String FIND_BY_TITLE_AND_AUTHOR_SQL = """
            SELECT B.BOOKID, B.TITLE, B.PRICE, B.STOCKQUANTITY, B.AUTHORID, B.CATEGORYID
            FROM BOOK B
            JOIN AUTHOR A ON B.AUTHORID = A.AUTHORID
            WHERE B.TITLE = ? AND A.AUTHORNAME = ?
            """;

    private static final String FIND_BY_CATEGORY_AND_AUTHOR_SQL = """
            SELECT B.BOOKID, B.TITLE, B.PRICE, B.STOCKQUANTITY, B.AUTHORID, B.CATEGORYID
            FROM BOOK B
            JOIN CATEGORY C ON B.CATEGORYID = C.CATEGORYID
            JOIN AUTHOR A ON B.AUTHORID = A.AUTHORID
            WHERE C.CATEGORYNAME = ? AND A.AUTHORNAME = ?
            """;

    private static final String FIND_BY_CATEGORY_AND_TITLE_SQL = """
            SELECT B.BOOKID, B.TITLE, B.PRICE, B.STOCKQUANTITY, B.AUTHORID, B.CATEGORYID
            FROM BOOK B
            JOIN CATEGORY C ON B.CATEGORYID = C.CATEGORYID
            WHERE C.CATEGORYNAME = ? AND B.TITLE = ?
            """;

    private static final String FIND_BY_CATEGORY_AND_TITLE_AND_AUTHOR_SQL = """
            SELECT B.BOOKID, B.TITLE, B.PRICE, B.STOCKQUANTITY, B.AUTHORID, B.CATEGORYID
            FROM BOOK B
            JOIN CATEGORY C ON B.CATEGORYID = C.CATEGORYID
            JOIN AUTHOR A ON B.AUTHORID = A.AUTHORID
            WHERE C.CATEGORYNAME = ? AND B.TITLE = ? AND A.AUTHORNAME = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public BookDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> rowMapper = (rs, rowNum) ->
            new Book(
                    rs.getInt("bookid"),
                    rs.getString("title"),
                    rs.getDouble("price"),
                    rs.getInt("stockquantity"),
                    rs.getInt("authorid"),
                    rs.getInt("categoryid")
            );


    /**
     * Adds a new book to the database.
     *
     * @param book
     * @return number of rows affected
     */
    @Override
    public int addBook(Book book) {
        return jdbcTemplate.update(
                INSERT_SQL,
                book.getTitle(),
                book.getPrice(),
                book.getStockQuantity(),
                book.getAuthorId(),
                book.getCategoryId()
        );
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId
     * @return number of rows affected
     */
    @Override
    public int deleteBook(int bookId) {
        return jdbcTemplate.update(DELETE_SQL, bookId);
    }

    /**
     * Updates all properties of an existing book.
     *
     * @param book
     * @return number of rows affected
     */
    @Override
    public int updateBookWithAllProp(Book book) {
        return jdbcTemplate.update(
                UPDATE_ALL_SQL,
                book.getTitle(),
                book.getPrice(),
                book.getStockQuantity(),
                book.getAuthorId(),
                book.getCategoryId(),
                book.getBookId()
        );
    }

    /**
     * Updates the stock quantity of a book.
     *
     * @param bookId
     * @param stockQuantity
     * @return number of rows affected
     */
    @Override
    public int updateBook(int bookId, int stockQuantity) {
        return jdbcTemplate.update(UPDATE_STOCK_SQL, stockQuantity, bookId);
    }

    /**
     * Retrieves all books from the database.
     *
     * @return a list of all books
     */
    @Override
    public List<Book> getAllBooks() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    /**
     * Finds a book by its ID.
     *
     * @param id
     * @return an Optional containing the book, or empty if not found
     */
    @Override
    public Optional<Book> getBookById(int id) {
        List<Book> results = jdbcTemplate.query(FIND_BY_ID_SQL, rowMapper, id);
        return results.stream().findFirst();
    }

    /**
     * Finds books by title.
     *
     * @param title
     * @return a list of matching books
     */
    @Override
    public List<Book> findByTitle(String title) {
        return jdbcTemplate.query(FIND_BY_TITLE_SQL, rowMapper, title);
    }

    /**
     * Finds books by author name.
     *
     * @param authorName
     * @return a list of matching books
     */
    @Override
    public List<Book> findByAuthorName(String authorName) {
        return jdbcTemplate.query(FIND_BY_AUTHOR_SQL, rowMapper, authorName);
    }

    /**
     * Finds books by category name.
     *
     * @param category the category name to search for
     * @return a list of matching books
     */
    @Override
    public List<Book> findByCategory(String category) {
        return jdbcTemplate.query(FIND_BY_CATEGORY_SQL, rowMapper, category);
    }

    /**
     * Finds books by title and author name.
     *
     * @param title
     * @param authorName
     * @return a list of matching books
     */
    @Override
    public List<Book> findByTitleAndAuthorName(String title, String authorName) {
        return jdbcTemplate.query(FIND_BY_TITLE_AND_AUTHOR_SQL, rowMapper, title, authorName);
    }

    /**
     * Finds books by category and author name.
     *
     * @param category
     * @param authorName
     * @return a list of matching books
     */
    @Override
    public List<Book> findByCategoryAndAuthorName(String category, String authorName) {
        return jdbcTemplate.query(FIND_BY_CATEGORY_AND_AUTHOR_SQL, rowMapper, category, authorName);
    }

    /**
     * Finds books by category and title.
     *
     * @param category
     * @param title
     * @return a list of matching books
     */
    @Override
    public List<Book> findByCategoryAndTitle(String category, String title) {
        return jdbcTemplate.query(FIND_BY_CATEGORY_AND_TITLE_SQL, rowMapper, category, title);
    }

    /**
     * Finds books by category, title, and author name.
     *
     * @param category
     * @param title
     * @param authorName
     * @return a list of matching books
     */
    @Override
    public List<Book> findByCategoryAndTitleAndAuthorName(String category, String title, String authorName) {
        return jdbcTemplate.query(FIND_BY_CATEGORY_AND_TITLE_AND_AUTHOR_SQL, rowMapper, category, title, authorName);
    }
}