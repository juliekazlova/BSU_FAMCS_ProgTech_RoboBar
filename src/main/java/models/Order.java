package models;

import java.util.Objects;

public class Order {
    private Product product;
    private OrderStatus status;
    private Client client;
    private int id;

    public Order(Product product, OrderStatus status, Client client, int id) {
        this.product = product;
        this.status = status;
        this.client = client;
        this.id = id;
    }

    public Order(Product product, OrderStatus status, Client client) {
        this.product = product;
        this.status = status;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "products=" + product +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(product, order.product) &&
                status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, status);
    }
}
