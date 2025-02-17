package org.example.threadCommunication;

import org.example.model.Customer;
import org.example.model.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StoreInventory {
    private final Map<Long, Product> inventory = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void addProduct(Product product) {
        lock.writeLock().lock();
        try {
            inventory.put(product.getId(), product);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Product getProduct(long productId) {
        lock.readLock().lock();
        try {
            return inventory.get(productId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean purchaseCart(Customer customer) {
        lock.writeLock().lock();
        try {
            for (Product cartProduct : customer.getCart().getProducts()) {
                Product stockProduct = inventory.get(cartProduct.getId());
                if (stockProduct == null) {
                    System.out.println("Product with ID " + cartProduct.getId() + " does not exist.");
                    return false;
                }
                if (stockProduct.getQuantity() < cartProduct.getQuantity()) {
                    System.out.println("Not enough stock for product: " + cartProduct.getName());
                    return false;
                }
            }
            for (Product cartProduct : customer.getCart().getProducts()) {
                Product stockProduct = inventory.get(cartProduct.getId());
                stockProduct.setQuantity(stockProduct.getQuantity() - cartProduct.getQuantity());
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void printInventory() {
        lock.readLock().lock();
        try {
            System.out.println("Store Inventory:");
            inventory.values().forEach(System.out::println);
        } finally {
            lock.readLock().unlock();
        }
    }
}