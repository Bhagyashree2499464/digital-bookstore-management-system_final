package com.cts.demo.dao.impl;

import com.cts.demo.dao.InventoryDao;
import com.cts.demo.model.Inventory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of {@link InventoryDao}.
 */
@Repository
public class InventoryDaoImpl implements InventoryDao {

    private static final String INSERT_SQL = "INSERT INTO INVENTORY (BOOKID, QUANTITY) VALUES (?, ?)";
    private static final String FIND_BY_BOOK_ID_SQL = "SELECT INVENTORYID, BOOKID, QUANTITY FROM INVENTORY WHERE BOOKID = ?";
    private static final String FIND_BY_BOOK_TITLE_SQL = """
            SELECT I.INVENTORYID, I.BOOKID, I.QUANTITY
            FROM INVENTORY I
            JOIN BOOK B ON I.BOOKID = B.BOOKID
            WHERE B.TITLE = ?
            """;
    private static final String FIND_ALL_SQL = "SELECT INVENTORYID, BOOKID, QUANTITY FROM INVENTORY";
    private static final String REDUCE_STOCK_SQL = """
            UPDATE INVENTORY
            SET QUANTITY = QUANTITY - ?
            WHERE BOOKID = ? AND QUANTITY >= ?
            """;
    private static final String DELETE_SQL = "DELETE FROM INVENTORY WHERE BOOKID = ?";
    private static final String UPDATE_QUANTITY_SQL = "UPDATE INVENTORY SET QUANTITY = ? WHERE BOOKID = ?";

    private final JdbcTemplate jdbcTemplate;

    public InventoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Inventory> rowMapper = (rs, rowNum) ->
            new Inventory(
                    rs.getInt("InventoryID"),
                    rs.getInt("BookID"),
                    rs.getInt("Quantity")
            );

    /**
     * Adds a new inventory record to the database.
     *
     * @param inventory
     * @return true if the record was added successfully, false otherwise
     */
    @Override
    public boolean addInventory(Inventory inventory) {
        return jdbcTemplate.update(
                INSERT_SQL,
                inventory.getBookID(),
                inventory.getQuantity()
        ) == 1;
    }

    /**
     * Retrieves inventory by book ID.
     *
     * @param bookId
     * @return an Optional containing the inventory, or empty if not found
     */
    @Override
    public Optional<Inventory> getInventoryByBookId(int bookId) {
        List<Inventory> result = jdbcTemplate.query(FIND_BY_BOOK_ID_SQL, rowMapper, bookId);
        return result.stream().findFirst();
    }

    /**
     * Retrieves inventory by book title.
     *
     * @param bookTitle
     * @return an Optional containing the inventory, or empty if not found
     */
    @Override
    public Optional<Inventory> getInventoryByBookTitle(String bookTitle) {
        List<Inventory> result = jdbcTemplate.query(FIND_BY_BOOK_TITLE_SQL, rowMapper, bookTitle);
        return result.stream().findFirst();
    }

    /**
     * Retrieves all inventory records from the database.
     *
     * @return a list of all inventory records
     */
    @Override
    public List<Inventory> getAllInventory() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    /**
     * Reduces the stock quantity of a book by the given amount.
     *
     * @param bookId
     * @param quantity
     * @return number of rows affected
     */
    @Override
    public int reduceStock(int bookId, int quantity) {
        Optional<Inventory> inventoryOpt = getInventoryByBookId(bookId);
        if (inventoryOpt.isEmpty()) {
            return 0;
        }
        return jdbcTemplate.update(REDUCE_STOCK_SQL, quantity, bookId, quantity);
    }

    /**
     * Deletes an inventory record by book ID.
     *
     * @param bookId
     * @return number of rows affected
     */
    @Override
    public int deleteInventoryBookId(int bookId) {
        Optional<Inventory> inventoryOpt = getInventoryByBookId(bookId);
        if (inventoryOpt.isEmpty()) {
            return 0;
        }
        return jdbcTemplate.update(DELETE_SQL, bookId);
    }

    /**
     * Checks whether the stock for a book is low (less than 1).
     *
     * @param bookId
     * @return true if stock is low or not found, false otherwise
     */
    @Override
    public boolean isStockLow(int bookId) {
        Optional<Inventory> inventoryOpt = getInventoryByBookId(bookId);
        return inventoryOpt.isEmpty() || inventoryOpt.get().getQuantity() < 1;
    }

    /**
     * Checks whether the available stock is less than the requested order quantity.
     *
     * @param bookId
     * @param orderQuantity
     * @return true if stock is insufficient, false otherwise
     */
    @Override
    public boolean isOutOfStock(int bookId, int orderQuantity) {
        Optional<Inventory> inventoryOpt = getInventoryByBookId(bookId);
        if (inventoryOpt.isEmpty()) {
            return true;
        }
        return inventoryOpt.get().getQuantity() < orderQuantity;
    }

    /**
     * Updates the stock quantity of a book.
     * If no inventory record exists, a new one is created.
     *
     * @param bookId
     * @param quantity
     * @return number of rows affected
     */
    @Override
    public int updateQuantity(int bookId, int quantity) {
        Optional<Inventory> inventoryOpt = getInventoryByBookId(bookId);
        //this code should be changed under validation
        if (inventoryOpt.isEmpty()) {
            addInventory(new Inventory(bookId, quantity));
        }
        return jdbcTemplate.update(UPDATE_QUANTITY_SQL, quantity, bookId);
    }
}