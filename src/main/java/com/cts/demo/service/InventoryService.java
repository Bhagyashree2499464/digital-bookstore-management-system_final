package com.cts.demo.service;

import com.cts.demo.model.Book;
import com.cts.demo.model.Inventory;
import java.util.List;
import java.util.Optional;

public interface InventoryService {
    boolean addInventory(Inventory inventory) ;
    Optional<Inventory> getInventoryByBookId(int bookId) ;
    Optional<Inventory> getInventoryByBookTitle(String bookTitle);
    List<Inventory> getAllInventory() ;
    int updateQuantity(int bookId, int quantity);
    int reduceStock(int bookId, int quantity);
    int deleteInventoryBookId(int bookId);
    boolean isStockLow(int bookId);
    boolean isOutOfStock(int bookId, int orderQuantity);
}
