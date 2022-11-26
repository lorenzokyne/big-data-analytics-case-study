package models;

import org.jkarma.model.Transaction;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Relevation implements Serializable, Transaction<ClimateData> {
    private static int lastTid = 0;

    @Override
    public String toString() {
        return "Relevation{" +
                "tid=" + tid +
                ", timestamp=" + timestamp +
                ", reads=" + reads +
                '}';
    }

    public int tid;
    public Instant timestamp;
    public Set<ClimateData> reads;

    public Relevation(List<ClimateData> data) {
        this.tid = Relevation.getNextTid();
        this.timestamp = Instant.now();
        this.reads = data.stream().map(ClimateData::new).collect(Collectors.toSet());
    }

    public static int getNextTid() {
        int value = Relevation.lastTid;
        Relevation.lastTid++;
        return value;
    }

    @Override
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override
    public Collection<ClimateData> getItems() {
        return this.reads;
    }

    @Override
    public int getId() {
        return this.tid;
    }

    @Override
    public Iterator<ClimateData> iterator() {
        return this.reads.iterator();
    }

    @Override
    public void forEach(Consumer<? super ClimateData> action) {
        Transaction.super.forEach(action);
    }

    @Override
    public Spliterator<ClimateData> spliterator() {
        return Transaction.super.spliterator();
    }
}