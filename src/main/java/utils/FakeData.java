package utils;

import models.Client;
import models.Ingredient;
import models.Product;
import models.User;

import java.util.ArrayList;
import java.util.Collection;

public class FakeData {
    public Collection<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        products.add(Product.getDefaultProduct());
        products.add(Product.getDefaultProduct());
        products.add(Product.getDefaultProduct());
        return products;
    }

    public void orderProduct(Product product, Client currentUser) {
        System.out.println("Ordered " + product + " for user: " + currentUser);
    }
}
