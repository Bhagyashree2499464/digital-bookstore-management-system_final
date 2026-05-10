package com.cts.demo.dao;

import com.cts.demo.model.OrderItem;
import com.cts.demo.model.Orders;
import java.util.List;

public interface OrderDao {

    void placeOrder(Orders order);

    Orders getOrderById(int orderId);

    List<Orders> getOrdersByUserId(int userId);

    List<Orders> getAllOrders();

    List<OrderItem> getOrderItems(int orderId);

    void updateOrderStatus(int orderId, String status);

    boolean updateOrder(Orders order);

    void deleteOrder(int orderId);
}