package org.example.parallelProcessing;

import org.example.config.ExecutorServiceConfig;
import org.example.model.Customer;
import org.example.tasks.PurchaseTask;
import org.example.threadCommunication.StoreInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConcurrentPurchaseProcessing {
    private final StoreInventory inventory;

    public ConcurrentPurchaseProcessing(StoreInventory inventory) {
        this.inventory = inventory;
    }

    public void processPurchases(List<Customer> customers) {
        ExecutorService executorService = ExecutorServiceConfig.getExecutorService();
        List<Future<Void>> futures = new ArrayList<>();

        for (Customer customer : customers) {
            PurchaseTask task = new PurchaseTask(customer, inventory);
            futures.add(executorService.submit(task));
        }

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error processing purchase: " + e.getMessage());
            }
        }
    }
}