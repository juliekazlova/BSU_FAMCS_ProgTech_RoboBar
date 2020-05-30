package models;

import java.util.Collection;
import java.util.Objects;

public class Order {
    private Collection<Product> products; //а может пусть по одному заказывает?
    private OrderStatus status;
    //private Client forWhom; todo think about

    public Collection<Product> getProducts() {
        return products;
    }

    public void setProducts(Collection<Product> products) {
        this.products = products;
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
                "products=" + products +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(products, order.products) &&
                status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(products, status);
    }
}
