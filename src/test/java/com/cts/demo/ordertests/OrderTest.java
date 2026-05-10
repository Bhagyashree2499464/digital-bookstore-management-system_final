package com.cts.demo.ordertests;

import com.cts.demo.dao.OrderDao;
import com.cts.demo.exception.InvalidOrderException;
import com.cts.demo.exception.InvalidOrderStatusException;
import com.cts.demo.exception.OrderNotFoundException;
import com.cts.demo.model.OrderItem;
import com.cts.demo.model.Orders;
import com.cts.demo.service.impl.OrderServiceImpl;
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
 * Unit tests for {@link OrderServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Orders order;

    /**
     * Sets up a sample order before each test.
     */
    @BeforeEach
    void setUp() {
        order = new Orders();
        order.setOrderId(1);
        order.setUserId(10);
        order.setTotalAmount(1000);
        order.setStatus("PENDING");
    }

    /**
     * Tests that placeOrder calls DAO when order is valid.
     */
    @Test
    void placeOrder_shouldCallDao_whenOrderValid() {
        orderService.placeOrder(order);

        verify(orderDao).placeOrder(order);
        assertEquals("PENDING", order.getStatus());
    }

    /**
     * Tests that placeOrder throws InvalidOrderException when order is null.
     */
    @Test
    void placeOrder_shouldThrowException_whenOrderNull() {

        assertThrows(
                InvalidOrderException.class,
                () -> orderService.placeOrder(null)
        );

        verifyNoInteractions(orderDao);
    }

    /**
     * Tests that placeOrder throws InvalidOrderException when total amount is zero.
     */
    @Test
    void placeOrder_shouldThrowException_whenTotalAmountInvalid() {

        order.setTotalAmount(0);

        assertThrows(
                InvalidOrderException.class,
                () -> orderService.placeOrder(order)
        );

        verifyNoInteractions(orderDao);
    }

    /**
     * Tests that getOrderById returns order when ID exists.
     */
    @Test
    void getOrderById_shouldReturnOrder() {
        when(orderDao.getOrderById(1)).thenReturn(order);

        Orders result = orderService.getOrderById(1);

        assertNotNull(result);
        assertEquals(1, result.getOrderId());
    }

    /**
     * Tests that getOrderById throws OrderNotFoundException when not found.
     */
    @Test
    void getOrderById_shouldThrowException_whenNotFound() {

        when(orderDao.getOrderById(99)).thenReturn(null);

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderById(99)
        );
    }

    /**
     * Tests that getOrdersByUserId returns list of orders for a user.
     */
    @Test
    void getOrdersByUserId_shouldReturnOrders() {
        when(orderDao.getOrdersByUserId(10))
                .thenReturn(List.of(order));

        List<Orders> result =
                orderService.getOrdersByUserId(10);

        assertEquals(1, result.size());
    }

    /**
     * Tests that getAllOrders returns all orders.
     */
    @Test
    void getAllOrders_shouldReturnAllOrders() {
        when(orderDao.getAllOrders())
                .thenReturn(List.of(order));

        List<Orders> result =
                orderService.getAllOrders();

        assertEquals(1, result.size());
    }

    /**
     * Tests that updateOrderStatus updates when status is valid.
     */
    @Test
    void updateOrderStatus_shouldUpdate_whenStatusValid() {
        orderService.updateOrderStatus(1, "SHIPPED");

        verify(orderDao).updateOrderStatus(1, "SHIPPED");
    }

    /**
     * Tests that updateOrderStatus throws InvalidOrderStatusException when status is invalid.
     */
    @Test
    void updateOrderStatus_shouldThrowException_whenStatusInvalid() {

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.updateOrderStatus(1, "INVALID")
        );

        verify(orderDao, never()).updateOrderStatus(anyInt(), anyString());
    }

    /**
     * Tests that cancelOrder cancels the order when order is valid.
     */
    @Test
    void cancelOrder_shouldCancel_whenOrderValid() {
        when(orderDao.getOrderById(1)).thenReturn(order);

        orderService.cancelOrder(1);

        verify(orderDao).updateOrderStatus(1, "CANCELLED");
    }

    /**
     * Tests that cancelOrder throws InvalidOrderException when order is already delivered.
     */
    @Test
    void cancelOrder_shouldThrowException_whenOrderDelivered() {

        order.setStatus("DELIVERED");
        when(orderDao.getOrderById(1)).thenReturn(order);

        assertThrows(
                InvalidOrderException.class,
                () -> orderService.cancelOrder(1)
        );

        verify(orderDao, never()).updateOrderStatus(anyInt(), anyString());
    }

    /**
     * Tests that cancelOrder does not cancel when order is not found.
     */
    @Test

    void cancelOrder_shouldNotCancel_whenOrderNotFound() {
        when(orderDao.getOrderById(99)).thenReturn(null);

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.cancelOrder(99)
        );

        verify(orderDao, never()).updateOrderStatus(anyInt(), anyString());
    }

    /**
     * Tests that getOrderItems returns list of order items.
     */
    @Test
    void getOrderItems_shouldReturnItems() {
        OrderItem item = new OrderItem();
        item.setOrderItemId(1);

        when(orderDao.getOrderItems(1))
                .thenReturn(List.of(item));

        List<OrderItem> result =
                orderService.getOrderItems(1);

        assertEquals(1, result.size());
    }

    /**
     * Tests that cancelOrder throws OrderNotFoundException when order not found.
     */
    @Test
    void cancelOrder_shouldThrowException_whenOrderNotFound() {

        when(orderDao.getOrderById(99)).thenReturn(null);

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.cancelOrder(99)
        );

        verify(orderDao, never()).updateOrderStatus(anyInt(), anyString());
    }

    /**
     * Tests that updateOrder returns true when update is successful.
     */
    @Test
    void updateOrder_shouldReturnTrue() {
        when(orderDao.updateOrder(order)).thenReturn(true);

        boolean result =
                orderService.updateOrder(order);

        assertTrue(result);
    }
}