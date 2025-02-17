package org.example.model;

public class Customer {
    private long id;
    private String name;
    private final Cart cart;

    public Customer(long id, String name) {
        this.id = id;
        this.name = name;
        this.cart = new Cart();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cart getCart() {
        return cart;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cart=" + cart +
                '}';
    }
}