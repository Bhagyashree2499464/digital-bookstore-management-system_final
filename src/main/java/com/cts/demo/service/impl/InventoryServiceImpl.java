package com.cts.demo.service.impl;

import com.cts.demo.dao.InventoryDao;
import com.cts.demo.exception.InvalidInventoryDataException;
import com.cts.demo.exception.InventoryNotFoundException;
import com.cts.demo.exception.OutOfStockException;
import com.cts.demo.model.Inventory;
import com.cts.demo.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log =
            LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryDao inventoryDAO;

    public InventoryServiceImpl(InventoryDao inventoryDAO) {
        this.inventoryDAO = inventoryDAO;
        log.info("\nInventoryServiceImpl initialized");
    }

    /**
     * Adds a new inventory record.
     *
     * @param inventory
     * @return true if added successfully, false otherwise
     */
    @Override
    public boolean addInventory(Inventory inventory) {
        log.info("\nRequest received to add inventory: {}", inventory);
        if (inventory.getQuantity() <= 0) {
            throw new InvalidInventoryDataException("Quantity must be greater than zero");
        }

        boolean success = inventoryDAO.addInventory(inventory);

        if (success) {
            log.debug("\nInventory added successfully [InventoryId={}, BookId={}, Quantity={}]",
                    inventory.getInventoryID(),
                    inventory.getBookID(),
                    inventory.getQuantity());
        } else {
            log.warn("\nFailed to add inventory with InventoryId={}",
                    inventory.getInventoryID());
        }
        return success;
    }

    /**
     * Retrieves inventory by book ID.
     *
     * @param bookId
     * @return an Optional containing the inventory
     */
    @Override
    public Optional<Inventory> getInventoryByBookId(int bookId) {
        log.info("\nFetching inventory for BookId={}", bookId);

        Optional<Inventory> inventory = inventoryDAO.getInventoryByBookId(bookId);

        if (inventory.isPresent()) {
            Inventory inv = inventory.get();
            log.debug("\nInventory found [InventoryId={}, BookId={}, Quantity={}]",
                    inv.getInventoryID(),
                    inv.getBookID(),
                    inv.getQuantity());
        } else {
            log.warn("\nNo inventory found for BookId={}", bookId);
            throw new InventoryNotFoundException("Inventory not found for BookId: " + bookId);
        }
        return inventory;
    }

    /**
     * Retrieves inventory by book title.
     *
     * @param bookTitle
     * @return an Optional containing the inventory
     */
    @Override
    public Optional<Inventory> getInventoryByBookTitle(String bookTitle) {
        log.info("\nFetching inventory for BookName={}", bookTitle);

        Optional<Inventory> inventory = inventoryDAO.getInventoryByBookTitle(bookTitle);

        if (inventory.isPresent()) {
            Inventory inv = inventory.get();
            log.debug("\nInventory found [InventoryId={}, BookId={}, Quantity={}]",
                    inv.getInventoryID(),
                    inv.getBookID(),
                    inv.getQuantity());
        } else {
            log.warn("\nNo inventory found for BookName{}", bookTitle);
        }
        return inventory;
    }

    /**
     * Retrieves all inventory records.
     *
     * @return a list of all inventory records
     */
    @Override
    public List<Inventory> getAllInventory() {
        log.info("\nFetching all inventory records");

        List<Inventory> inventories = inventoryDAO.getAllInventory();

        log.debug("\nTotal inventory records fetched = {}", inventories.size());
        return inventories;
    }

    /**
     * Updates the stock quantity for a book.
     *
     * @param bookId
     * @param quantity
     * @return number of rows affected
     */
    @Override
    public int updateQuantity(int bookId, int quantity) {
        log.info("\nUpdating quantity for BookId={}, NewQuantity={}", bookId, quantity);
        if (quantity < 0) {
            throw new InvalidInventoryDataException("Quantity cannot be negative");
        }

        if (inventoryDAO.getInventoryByBookId(bookId).isEmpty()) {
            throw new InventoryNotFoundException("Inventory not found for BookId: " + bookId);
        }
        int rows = inventoryDAO.updateQuantity(bookId, quantity);

        if (rows > 0) {
            log.debug("\nQuantity updated successfully for BookId={}", bookId);
        } else {
            log.warn("\nFailed to update quantity for BookId={}", bookId);
        }
        return rows;
    }

    /**
     * Reduces stock for a book by the given quantity.
     *
     * @param bookId
     * @param quantity
     * @return number of rows affected
     */
    @Override
    public int reduceStock(int bookId, int quantity) {
        log.info("\nReducing stock for BookId={}, ReductionBy={}", bookId, quantity);
        Inventory inventory = inventoryDAO.getInventoryByBookId(bookId)
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found for BookId: " + bookId));

        if (inventory.getQuantity() < quantity) {
            throw new OutOfStockException("Not enough stock for BookId: " + bookId);
        }
        int rows = inventoryDAO.reduceStock(bookId, quantity);

        if (rows > 0) {
            log.debug("\nStock reduced successfully for BookId={}", bookId);
        } else {
            log.warn("\nFailed to reduce stock for BookId={}", bookId);
        }
        return rows;
    }

    /**
     * Deletes the inventory record for a book.
     *
     * @param bookId
     * @return number of rows affected
     */
    @Override
    public int deleteInventoryBookId(int bookId) {
        log.info("\nDeleting inventory for BookId={}", bookId);

        if (inventoryDAO.getInventoryByBookId(bookId).isEmpty()) {
            throw new InventoryNotFoundException("Inventory not found for BookId: " + bookId);
        }

        int rows = inventoryDAO.deleteInventoryBookId(bookId);

        if (rows > 0) {
            log.debug("\nInventory deleted successfully for BookId={}", bookId);
        } else {
            log.warn("\nNo inventory found to delete for BookId={}", bookId);
        }
        return rows;
    }

    /**
     * Checks whether stock for a book is low.
     *
     * @param bookId
     * @return true if stock is low, false otherwise
     */
    @Override
    public boolean isStockLow(int bookId) {
        return inventoryDAO.isStockLow(bookId);
    }

    /**
     * Checks whether stock is insufficient for the requested quantity.
     *
     * @param bookId
     * @param orderQuantity
     * @return true if out of stock, false otherwise
     */
    @Override
    public boolean isOutOfStock(int bookId, int orderQuantity) {
        return inventoryDAO.isOutOfStock(bookId, orderQuantity);
    }
}
