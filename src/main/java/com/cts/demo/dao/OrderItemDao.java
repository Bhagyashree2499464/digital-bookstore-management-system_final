package com.cts.demo.dao;

import com.cts.demo.model.OrderItem;
import java.util.List;

public interface OrderItemDao {

    void addOrderItem(OrderItem orderItem);

    OrderItem getOrderItemById(int orderItemId);

    List<OrderItem> getOrderItemsByOrderId(int orderId);

    void updateOrderItem(OrderItem orderItem);

    void deleteOrderItem(int orderItemId);
}