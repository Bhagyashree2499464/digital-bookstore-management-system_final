package com.cts.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Inventory {
    private int InventoryID;
    private int BookID;
    private int Quantity;

    public Inventory(int inventoryID, int bookID, int quantity) {
        InventoryID = inventoryID;
        BookID = bookID;
        Quantity = quantity;
    }

    public Inventory(int bookID, int quantity) {
        BookID = bookID;
        Quantity = quantity;
    }
}
