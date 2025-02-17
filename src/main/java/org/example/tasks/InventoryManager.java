package org.example.tasks;

import org.example.model.Product;
import org.example.threadCommunication.StoreInventory;

import java.util.List;

public class InventoryManager {
    private final StoreInventory inventory;

    public InventoryManager(StoreInventory inventory) {
        this.inventory = inventory;
    }

    public void initializeInventory(List<Product> products) {
        products.forEach(inventory::addProduct);
    }

    public void displayInventory() {
        inventory.printInventory();
    }
}