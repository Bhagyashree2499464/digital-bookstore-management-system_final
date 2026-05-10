package com.cts.demo.service.impl;

import com.cts.demo.dao.OrderItemDao;
import com.cts.demo.exception.InvalidOrderItemException;
import com.cts.demo.exception.OrderItemNotFoundException;
import com.cts.demo.model.OrderItem;
import com.cts.demo.service.OrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private OrderItemDao orderItemDao;

    public OrderItemServiceImpl(OrderItemDao orderItemDao) {
        this.orderItemDao = orderItemDao;
    }

    /**
     * Adds a new order item.
     *
     * @param orderItem
     */
    @Override
    public void addOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new InvalidOrderItemException("OrderItem cannot be null");
        }
        if (orderItem.getQuantity() <= 0) {
            throw new InvalidOrderItemException("Quantity must be greater than 0");
        }
        if (orderItem.getUnitPrice() <= 0) {
            throw new InvalidOrderItemException("Unit price must be greater than 0");
        }
        orderItemDao.addOrderItem(orderItem);
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param orderItemId
     * @return the matching order item
     */
    @Override
    public OrderItem getOrderItemById(int orderItemId) {
        OrderItem item = orderItemDao.getOrderItemById(orderItemId);
        if (item == null) {
            throw new OrderItemNotFoundException(
                    "OrderItem not found with ID: " + orderItemId);
        }
        return item;
    }

    /**
     * Retrieves all items for a specific order.
     *
     * @param orderId
     * @return a list of order items
     */
    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = orderItemDao.getOrderItemsByOrderId(orderId);
        if (items.isEmpty()) {
            log.warn("No items found for OrderID: {}", orderId);
        }
        return items;
    }

    /**
     * Updates an existing order item.
     *
     * @param orderItem
     */
    @Override
    public void updateOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            log.error("OrderItem cannot be null.");
            return;
        }
        orderItemDao.updateOrderItem(orderItem);
    }

    /**
     * Removes an order item by its ID.
     *
     * @param orderItemId
     */
    @Override
    public void removeOrderItem(int orderItemId) {
        OrderItem item = orderItemDao.getOrderItemById(orderItemId);
        if (item == null) {
            throw new OrderItemNotFoundException(
                    "OrderItem not found with ID: " + orderItemId);
        }
        orderItemDao.deleteOrderItem(orderItemId);
    }
}