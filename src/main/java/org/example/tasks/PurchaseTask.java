package org.example.tasks;

import org.example.model.Customer;
import org.example.threadCommunication.StoreInventory;
import java.util.concurrent.Callable;

public class PurchaseTask implements Callable<Void> {
    private final Customer customer;
    private final StoreInventory inventory;

    public PurchaseTask(Customer customer, StoreInventory inventory) {
        this.customer = customer;
        this.inventory = inventory;
    }

    @Override
    public Void call() {
        System.out.println("Processing purchase for customer: " + customer.getName());
        boolean success = inventory.purchaseCart(customer);
        if (success) {
            System.out.println("Purchased cart for customer: " + customer.getName());
        } else {
            System.err.println("Failed to purchase cart for customer: " + customer.getName());
        }
        return null;
    }
}