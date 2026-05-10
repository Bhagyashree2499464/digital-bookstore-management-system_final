package com.cts.demo.inventorytests;

import com.cts.demo.dao.InventoryDao;
import com.cts.demo.exception.InvalidInventoryDataException;
import com.cts.demo.exception.InventoryNotFoundException;
import com.cts.demo.exception.OutOfStockException;
import com.cts.demo.model.Inventory;
import com.cts.demo.service.impl.InventoryServiceImpl;
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
 * Unit tests for {@link InventoryServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class InventoryTest {

    @Mock
    private InventoryDao inventoryDao;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory inventory;

    /**
     * Sets up a sample inventory before each test.
     */
    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setInventoryID(1);
        inventory.setBookID(101);
        inventory.setQuantity(20);
    }

    /**
     * Tests that addInventory returns true when inventory is added successfully.
     */
    @Test
    void addInventory_shouldReturnTrue_whenInventoryAdded() {
        when(inventoryDao.addInventory(inventory)).thenReturn(true);

        boolean result = inventoryService.addInventory(inventory);

        assertTrue(result);
        verify(inventoryDao).addInventory(inventory);
    }

    /**
     * Tests that addInventory throws InvalidInventoryDataException when quantity is zero.
     */
    @Test
    void addInventory_shouldThrowException_whenQuantityInvalid() {

        inventory.setQuantity(0);

        assertThrows(
                InvalidInventoryDataException.class,
                () -> inventoryService.addInventory(inventory)
        );

        verify(inventoryDao, never()).addInventory(any());
    }

    /**
     * Tests that getInventoryByBookId returns inventory when book ID exists.
     */
    @Test
    void getInventoryByBookId_shouldReturnInventory() {
        when(inventoryDao.getInventoryByBookId(101))
                .thenReturn(Optional.of(inventory));

        Optional<Inventory> result =
                inventoryService.getInventoryByBookId(101);

        assertTrue(result.isPresent());
        assertEquals(101, result.get().getBookID());
    }

    /**
     * Tests that getInventoryByBookId throws InventoryNotFoundException when not found.
     */
    @Test
    void getInventoryByBookId_shouldThrowException_whenNotFound() {

        when(inventoryDao.getInventoryByBookId(999))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.getInventoryByBookId(999)
        );
    }

    /**
     * Tests that getAllInventory returns list of inventory records.
     */
    @Test
    void getAllInventory_shouldReturnInventoryList() {
        when(inventoryDao.getAllInventory())
                .thenReturn(List.of(inventory));

        List<Inventory> result =
                inventoryService.getAllInventory();

        assertEquals(1, result.size());
    }

    /**
     * Tests that updateQuantity returns updated rows when inventory exists.
     */
    @Test
    void updateQuantity_shouldReturnUpdatedRows_whenInventoryExists() {

        when(inventoryDao.getInventoryByBookId(101))
                .thenReturn(Optional.of(inventory));

        when(inventoryDao.updateQuantity(101, 40))
                .thenReturn(1);

        int result = inventoryService.updateQuantity(101, 40);

        assertEquals(1, result);
    }

    /**
     * Tests that updateQuantity throws InventoryNotFoundException when inventory not found.
     */
    @Test
    void updateQuantity_shouldThrowException_whenInventoryNotFound() {

        when(inventoryDao.getInventoryByBookId(999))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.updateQuantity(999, 20)
        );
    }

    /**
     * Tests that reduceStock returns updated rows when stock is sufficient.
     */
    @Test
    void reduceStock_shouldReturnUpdatedRows_whenStockIsSufficient() {

        when(inventoryDao.getInventoryByBookId(101))
                .thenReturn(Optional.of(inventory));

        when(inventoryDao.reduceStock(101, 5))
                .thenReturn(1);

        int result = inventoryService.reduceStock(101, 5);

        assertEquals(1, result);
    }

    /**
     * Tests that reduceStock throws OutOfStockException when stock is insufficient.
     */
    @Test
    void reduceStock_shouldThrowException_whenStockIsInsufficient() {

        inventory.setQuantity(3);

        when(inventoryDao.getInventoryByBookId(101))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                OutOfStockException.class,
                () -> inventoryService.reduceStock(101, 5)
        );
    }

    /**
     * Tests that deleteInventoryBookId returns deleted rows when inventory exists.
     */
    @Test
    void deleteInventoryBookId_shouldReturnDeletedRows_whenInventoryExists() {

        when(inventoryDao.getInventoryByBookId(101))
                .thenReturn(Optional.of(inventory));

        when(inventoryDao.deleteInventoryBookId(101))
                .thenReturn(1);

        int result = inventoryService.deleteInventoryBookId(101);

        assertEquals(1, result);
    }

    /**
     * Tests that deleteInventoryBookId throws InventoryNotFoundException when not found.
     */
    @Test
    void deleteInventoryBookId_shouldThrowException_whenInventoryNotFound() {

        when(inventoryDao.getInventoryByBookId(999))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.deleteInventoryBookId(999)
        );
    }

    /**
     * Tests that isStockLow returns true when stock is low.
     */
    @Test
    void isStockLow_shouldReturnTrue() {
        when(inventoryDao.isStockLow(101)).thenReturn(true);

        assertTrue(inventoryService.isStockLow(101));
    }

    /**
     * Tests that isOutOfStock returns true when stock is insufficient for order.
     */
    @Test
    void isOutOfStock_shouldReturnTrue() {
        when(inventoryDao.isOutOfStock(101, 25)).thenReturn(true);

        assertTrue(inventoryService.isOutOfStock(101, 25));
    }
}