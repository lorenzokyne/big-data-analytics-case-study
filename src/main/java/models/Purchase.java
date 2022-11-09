package models;

import com.google.common.collect.Sets;
import org.jkarma.model.Transaction;

import java.time.Instant;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Purchase implements Transaction<Product> {
    private static int lastTid = 0;

    @Override
    public String toString() {
        return "Purchase{" +
                "tid=" + tid +
                ", timestamp=" + timestamp +
                ", products=" + products +
                '}';
    }

    public int tid;
    public Instant timestamp;
    public Set<Product> products;

    public Purchase(Product... products) {
        this.tid = Purchase.getNextTid();
        this.timestamp = Instant.now();
        this.products = Sets.newHashSet(products);
    }

    public static int getNextTid() {
        int value = Purchase.lastTid;
        Purchase.lastTid++;
        return value;
    }

    @Override
    public int getId() {
        return this.tid;
    }

    @Override
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override
    public Set<Product> getItems() {
        return this.products;
    }

    @Override
    public Iterator<Product> iterator() {
        return this.products.iterator();
    }

    @Override
    public void forEach(Consumer<? super Product> action) {
        Transaction.super.forEach(action);
    }

    @Override
    public Spliterator<Product> spliterator() {
        return Transaction.super.spliterator();
    }
}