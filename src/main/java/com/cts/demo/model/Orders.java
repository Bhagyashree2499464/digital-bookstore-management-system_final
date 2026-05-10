package com.cts.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    private int orderId;
    private int userId;
    private Timestamp orderDate;
    private double totalAmount;
    private String status;

    List<OrderItem> orderItems;

}