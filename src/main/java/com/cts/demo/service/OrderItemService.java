package com.cts.demo.service;


import com.cts.demo.model.OrderItem;
import java.util.List;

public interface OrderItemService {

    void addOrderItem(OrderItem orderItem);

    OrderItem getOrderItemById(int orderItemId);

    List<OrderItem> getOrderItemsByOrderId(int orderId);

    void updateOrderItem(OrderItem orderItem);

    void removeOrderItem(int orderItemId);
}