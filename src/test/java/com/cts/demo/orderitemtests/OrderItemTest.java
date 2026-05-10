package com.cts.demo.orderitemtests;

import com.cts.demo.dao.OrderItemDao;
import com.cts.demo.exception.InvalidOrderItemException;
import com.cts.demo.exception.OrderItemNotFoundException;
import com.cts.demo.model.OrderItem;
import com.cts.demo.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderItemServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class OrderItemTest {

    @Mock
    private OrderItemDao orderItemDao;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem orderItem;

    /**
     * Sets up a sample order item before each test.
     */
    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
        orderItem.setOrderItemId(1);
        orderItem.setOrderId(10);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(500);
    }

    /**
     * Tests that addOrderItem calls DAO when order item is valid.
     */
    @Test
    void addOrderItem_shouldCallDao_whenValidOrderItem() {
        orderItemService.addOrderItem(orderItem);
        verify(orderItemDao).addOrderItem(orderItem);
    }

    /**
     * Tests that addOrderItem throws InvalidOrderItemException when order item is null.
     */
    @Test
    void addOrderItem_shouldThrowException_whenOrderItemIsNull() {

        assertThrows(
                InvalidOrderItemException.class,
                () -> orderItemService.addOrderItem(null)
        );

        verifyNoInteractions(orderItemDao);
    }

    /**
     * Tests that addOrderItem throws InvalidOrderItemException when unit price is invalid.
     */
    @Test
    void addOrderItem_shouldThrowException_whenUnitPriceInvalid() {

        orderItem.setUnitPrice(0);

        assertThrows(
                InvalidOrderItemException.class,
                () -> orderItemService.addOrderItem(orderItem)
        );

        verifyNoInteractions(orderItemDao);
    }

    /**
     * Tests that getOrderItemById returns order item when ID exists.
     */
    @Test
    void getOrderItemById_shouldReturnOrderItem() {
        when(orderItemDao.getOrderItemById(1)).thenReturn(orderItem);

        OrderItem result = orderItemService.getOrderItemById(1);

        assertNotNull(result);
        assertEquals(1, result.getOrderItemId());
    }

    /**
     * Tests that getOrderItemById throws OrderItemNotFoundException when not found.
     */
    @Test
    void getOrderItemById_shouldThrowException_whenNotFound() {

        when(orderItemDao.getOrderItemById(99)).thenReturn(null);

        assertThrows(
                OrderItemNotFoundException.class,
                () -> orderItemService.getOrderItemById(99)
        );
    }

    /**
     * Tests that getOrderItemsByOrderId returns list of order items.
     */
    @Test
    void getOrderItemsByOrderId_shouldReturnList() {
        when(orderItemDao.getOrderItemsByOrderId(10))
                .thenReturn(List.of(orderItem));

        List<OrderItem> result =
                orderItemService.getOrderItemsByOrderId(10);

        assertEquals(1, result.size());
    }

    /**
     * Tests that updateOrderItem calls DAO when order item is valid.
     */
    @Test
    void updateOrderItem_shouldCallDao_whenValidOrderItem() {
        orderItemService.updateOrderItem(orderItem);
        verify(orderItemDao).updateOrderItem(orderItem);
    }

    /**
     * Tests that updateOrderItem does not call DAO when order item is null.
     */
    @Test
    void updateOrderItem_shouldNotCallDao_whenNull() {
        orderItemService.updateOrderItem(null);
        verifyNoInteractions(orderItemDao);
    }

    /**
     * Tests that removeOrderItem deletes the item when it exists.
     */
    @Test
    void removeOrderItem_shouldDelete_whenItemExists() {
        when(orderItemDao.getOrderItemById(1)).thenReturn(orderItem);

        orderItemService.removeOrderItem(1);

        verify(orderItemDao).deleteOrderItem(1);
    }

    /**
     * Tests that removeOrderItem throws OrderItemNotFoundException when item not found.
     */
    @Test
    void removeOrderItem_shouldThrowException_whenItemNotFound() {

        when(orderItemDao.getOrderItemById(99)).thenReturn(null);

        assertThrows(
                OrderItemNotFoundException.class,
                () -> orderItemService.removeOrderItem(99)
        );

        verify(orderItemDao, never()).deleteOrderItem(anyInt());
    }
}