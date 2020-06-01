package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class Product implements Serializable {
    private String name;
    private Collection<Ingredient> ingredients;

    public Product(String name, Collection<Ingredient> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public Product() {
    }

    public static Product getDefaultProduct() {
        Product product = new Product();
        Ingredient water = new Ingredient("Water");
        Ingredient sugar = new Ingredient("Sugar");
        Ingredient coffee = new Ingredient("Coffee");
        Collection<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(water);
        ingredients.add(sugar);
        ingredients.add(coffee);
        product.setIngredients(ingredients);
        product.setName("Cappuccino");
        return product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Collection<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) &&
                Objects.equals(ingredients, product.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients);
    }
}
