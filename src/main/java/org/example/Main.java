package org.example;

import org.example.config.ExecutorServiceConfig;
import org.example.model.Customer;
import org.example.model.Product;
import org.example.parallelProcessing.ConcurrentPurchaseProcessing;
import org.example.tasks.InventoryManager;
import org.example.threadCommunication.StoreInventory;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("========== Initializing Store Inventory ==========");
        StoreInventory inventory = new StoreInventory();
        InventoryManager inventoryManager = new InventoryManager(inventory);

        List<Product> products = List.of(
                new Product(1, "Apple", 50, 0.5),
                new Product(2, "Banana", 30, 0.3),
                new Product(3, "Orange", 20, 0.7)
        );
        inventoryManager.initializeInventory(products);

        if (products.isEmpty()) {
            System.err.println("Error: Inventory is empty after initialization!");
        } else {
            System.out.println("Initial Inventory:");
            inventoryManager.displayInventory();
        }

        System.out.println("\n========== Creating Customers ==========");
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Customer customer = new Customer(i, "Customer " + i);

            Product apple = inventory.getProduct(1);
            Product banana = inventory.getProduct(2);

            if (apple != null) {
                customer.getCart().addProduct(
                        new Product(apple.getId(), apple.getName(), 5, apple.getPrice()),
                        apple.getQuantity()
                );
            }
            if (banana != null) {
                customer.getCart().addProduct(
                        new Product(banana.getId(), banana.getName(), 3, banana.getPrice()),
                        banana.getQuantity()
                );
            }
            customers.add(customer);
            System.out.println(customer);
        }

        System.out.println("\n========== Processing Purchases ==========");
        ConcurrentPurchaseProcessing purchaseProcessing = new ConcurrentPurchaseProcessing(inventory);
        purchaseProcessing.processPurchases(customers);

        System.out.println("\n========== Final Inventory ==========");
        inventoryManager.displayInventory();

        System.out.println("\n========== Shutting Down Executor Service ==========");
        ExecutorServiceConfig.shutdown();
    }
}