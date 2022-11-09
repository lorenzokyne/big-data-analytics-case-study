package models;

import java.util.Objects;

public class Product implements Comparable<Product> {
    public static final Product BREAD = new Product("bread");
    public static final Product JUICE = new Product("juice");
    public static final Product WINE = new Product("wine");
    public static final Product SUGAR = new Product("sugar");
    public static final Product CAKE = new Product("cake");
    public static final Product BEER = new Product("beer");
    public static final Product CAFFE = new Product("caffe");

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static String getRandomicName() {
        int random = (int) (Math.random() * 6);
        switch (random) {
            case 0:
                return BREAD.name;
            case 1:
                return JUICE.name;
            case 2:
                return WINE.name;
            case 3:
                return SUGAR.name;
            case 4:
                return BEER.name;
            case 5:
                return CAFFE.name;
            default:
                return CAKE.name;
        }
    }

    private String name;
    public boolean alreadyMixed;

    public Product(String n) {
        this.alreadyMixed = true;
        this.name = n;
    }

    public boolean isDrink() {
        return this.name.equalsIgnoreCase("juice") || this.name.equalsIgnoreCase("wine") || this.name.equalsIgnoreCase("beer") || this.name.equalsIgnoreCase("caffe");
    }

    public boolean isFood() {
        return !this.isDrink();
    }

    @Override
    public int compareTo(Product p) {
        return this.name.compareTo(p.name);
    }

    public String getName() {
        return name;
    }

    public boolean isAlreadyMixed() {
        return alreadyMixed;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Product) {
            Product p = (Product) o;
            result = (this.compareTo(p) == 0);
        } else {
            throw new ClassCastException();
        }

        return result;
    }

    @Override
    public String toString() {
        return this.name;
    }
}