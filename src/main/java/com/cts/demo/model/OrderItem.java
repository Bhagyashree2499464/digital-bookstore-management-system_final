package com.cts.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private int orderItemId;
    private int orderId;
    private int bookId;
    private int quantity;
    private double unitPrice;

}