package com.cts.demo.service;

import com.cts.demo.model.OrderItem;
import com.cts.demo.model.Orders;
import java.util.List;

public interface OrderService {

    void placeOrder(Orders order);

    Orders getOrderById(int orderId);

    List<Orders> getOrdersByUserId(int userId);

    List<Orders> getAllOrders();

    List<OrderItem> getOrderItems(int orderId);

    public boolean updateOrder(Orders order);

    void updateOrderStatus(int orderId, String status);

    void cancelOrder(int orderId);
}
