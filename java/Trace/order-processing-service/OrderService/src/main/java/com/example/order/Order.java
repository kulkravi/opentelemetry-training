package com.example.order;

public class Order {
    private String id;
    private String item;
    private Double price;
    private int quantity;

    // Constructors, getters, and setters
    public Order() {}

    public Order(String id, String item, int quantity) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String toString() {
        return "Order(id = " + getId() + " item = " + getItem() + " quantity = " + getQuantity() + " price = " + getPrice() + ")";
    }
}