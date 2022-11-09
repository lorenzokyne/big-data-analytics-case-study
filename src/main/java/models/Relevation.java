package models;

import com.google.common.collect.Sets;
import org.jkarma.model.Transaction;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Relevation implements Transaction<ClimateData> {
    private static int lastTid = 0;


    public int tid;
    public Instant timestamp;
    public Set<ClimateData> reads;

    public Relevation(List<ClimateData> data) {
        this.tid = Relevation.getNextTid();
        this.timestamp = Instant.now();
        this.reads = new HashSet<>(data);
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