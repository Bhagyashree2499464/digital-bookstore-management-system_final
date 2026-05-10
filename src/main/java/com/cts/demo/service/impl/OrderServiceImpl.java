package com.cts.demo.service.impl;

import com.cts.demo.dao.OrderDao;
import com.cts.demo.exception.InvalidOrderException;
import com.cts.demo.exception.InvalidOrderStatusException;
import com.cts.demo.exception.OrderNotFoundException;
import com.cts.demo.model.OrderItem;
import com.cts.demo.model.Orders;
import com.cts.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private OrderDao orderDao;

    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    /**
     * Places a new order with status set to PENDING.
     *
     * @param order
     */
    @Override
    public void placeOrder(Orders order) {
        if (order == null) {
            throw new InvalidOrderException("Order cannot be null");
        }
        if (order.getTotalAmount() <= 0) {
            throw new InvalidOrderException("Total amount must be greater than 0");
        }

        order.setStatus("PENDING");
        orderDao.placeOrder(order);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId
     * @return the matching order
     */
    @Override
    public Orders getOrderById(int orderId) {
        Orders order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException(
                    "Order not found with ID: " + orderId);
        }
        return order;
    }

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId
     * @return a list of orders
     */
    @Override
    public List<Orders> getOrdersByUserId(int userId) {
        List<Orders> orders = orderDao.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            log.warn("No orders found for UserID: {}", userId);
        }
        return orders;
    }

    /**
     * Retrieves all orders.
     *
     * @return a list of all orders
     */
    @Override
    public List<Orders> getAllOrders() {
        return orderDao.getAllOrders();
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId
     * @param status
     */
    @Override
    public void updateOrderStatus(int orderId, String status) {
        if (!status.equals("PENDING") &&
                !status.equals("SHIPPED") &&
                !status.equals("DELIVERED") &&
                !status.equals("CANCELLED")) {

            throw new InvalidOrderStatusException(
                    "Invalid status: " + status);
        }

        orderDao.updateOrderStatus(orderId, status);
    }

    /**
     * Cancels an order if it has not been delivered.
     *
     * @param orderId
     */
    @Override
    public void cancelOrder(int orderId) {
        Orders order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException(
                    "Order not found with ID: " + orderId);
        }

        if ("DELIVERED".equals(order.getStatus())) {
            throw new InvalidOrderException(
                    "Cannot cancel a delivered order");
        }

        orderDao.updateOrderStatus(orderId, "CANCELLED");
        log.info("Order cancelled successfully. ID: {}", orderId);
    }

    /**
     * Retrieves all items for a specific order.
     *
     * @param orderId
     * @return a list of order items
     */
    @Override
    public List<OrderItem> getOrderItems(int orderId){
        return orderDao.getOrderItems(orderId);
    }

    /**
     * Updates the full order record.
     *
     * @param order
     * @return true if update was successful, false otherwise
     */
    @Override
    public boolean updateOrder(Orders order) {
        return orderDao.updateOrder(order);
    }
}