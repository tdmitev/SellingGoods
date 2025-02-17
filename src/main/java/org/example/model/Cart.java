package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<Product> products;

    public Cart() {
        this.products = new ArrayList<>();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product, int availableQuantity) {
        if (product.getQuantity() > availableQuantity) {
            System.err.println("Not enough stock for product: " + product.getName());
            return;
        }
        this.products.add(product);
    }

    public double calculateTotalPrice() {
        return products.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "products=" + products +
                '}';
    }
}