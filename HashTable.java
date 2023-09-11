/*
 * @author Myles Grant - grantmx@bc.edu
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class HashTable<K extends Comparable<K>, V> implements Map<K, V> {
    public static class Entry<K extends Comparable<K>, V> implements MapEntry<K, V>, Comparable<Entry<K, V>> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public int compareTo(Entry<K, V> other) {
            return key.compareTo(other.getKey());
        }

        @Override
        public String toString() {
            return key.toString() + ": " + value.toString();
        }
    }

    private class KeyIterator implements java.util.Iterator<K> {
        private Iterator<MapEntry<K, V>> entries = entrySet().iterator();

        @Override
        public K next() {
            return entries.next().getKey();
        }

        @Override
        public boolean hasNext() {
            return entries.hasNext();
        }
    }

    private class KeyIterable implements Iterable<K> {
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
    }

    private class ValueIterator implements java.util.Iterator<V> {
        Iterator<MapEntry<K, V>> entries = entrySet().iterator();

        @Override
        public V next() {
            return entries.next().getValue();
        }

        @Override
        public boolean hasNext() {
            return entries.hasNext();
        }
    }

    private class ValueIterable implements Iterable<V> {
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
    }

    @Override
    public V remove(K key) {
        if (isEmpty()) {
            return null;
        }
        int index = findIndex(key);
        if (table[index] != null) {
            for (int i = 0; i < table[index].size(); ++i) {
                if (table[index].get(i).getKey().compareTo(key) == 0) {
                    V value = table[index].remove(i).getValue();
                    --size;
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private int findIndex(K key) {
        return (Math.abs(key.hashCode() * scale + shift) % PRIME) % table.length;
    }

    private void resizeTable(Resize d) {
        System.out.println("RESIZING");
        if (d == Resize.UP) {
            capacity *= 2;
        } else if (capacity > 2) {
            capacity /= 2;
        }
        int primeCap = 0;
        PrimeFinder pf = new PrimeFinder((int) Math.ceil(capacity * 1.5));
        while (pf.hasNext() && primeCap < capacity) {
            primeCap = pf.next();
        }
        capacity = primeCap;
        ArrayList<Entry<K, V>>[] newTable = createTable();
        for (ArrayList<Entry<K, V>> l : table) {
            if (l != null) {
                for (Entry<K, V> each : l) {
                    put(each.getKey(), each.getValue(), newTable);
                }
            }
        }
        table = newTable;
    }

    @Override
    public Iterable<V> values() {
        return new ValueIterable();
    }

    @Override
    public Iterable<K> keySet() {
        return new KeyIterable();
    }

    public Iterable<MapEntry<K, V>> entrySet() {
        ArrayList<MapEntry<K, V>> snap = new ArrayList<>();
        for (ArrayList<Entry<K, V>> b : table) {
            if (b != null) {
                for (Entry<K, V> each : b) {
                    snap.add(each);
                }
            }
        }
        return snap;
    }

    private enum Resize {UP, DOWN};
    private static final double MAX_LOAD_FACTOR = 0.7;
    private static final double MIN_LOAD_FACTOR = 0.2;
    private static final int INITIAL_CAPACITY = 17;
    private int capacity = INITIAL_CAPACITY;
    private static final int PRIME = 109345121;
    private ArrayList<Entry<K, V>>[] table;
    private int scale;
    private int shift;
    private int size;
    private double loadFactor;

    @SuppressWarnings("unchecked")
    public HashTable(boolean repeatable) {
        table = (ArrayList<Entry<K, V>>[]) new ArrayList[INITIAL_CAPACITY];
        Random random = repeatable ? new Random(1) : new Random();
        scale = random.nextInt(PRIME - 1) + 1;
        shift = random .nextInt(PRIME);
    }

    public HashTable() {
        this(true);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Entry<K, V>>[] createTable() {
        ArrayList<Entry<K, V>>[] newTable = (ArrayList<Entry<K, V>>[]) new ArrayList[capacity];
        size = 0;
        return newTable;
    }

    @Override
    public V get(K key) {
        ArrayList<Entry<K, V>> list = table[findIndex(key)];
        if (list == null) {
            return null;
        } else {
            for (Entry<K, V> each : list) {
                if (each.getKey().compareTo(key) == 0) {
                    return each.getValue();
                }
            }
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        V answer = put(key, value, table);
        if (loadFactor > MAX_LOAD_FACTOR) {
            resizeTable(Resize.UP);
        }
        return answer;
    }

    private V put(K key, V value, ArrayList<Entry<K, V>>[] htable) {
        int index = findIndex(key);
        if (htable[index] != null) {
            for (Entry<K, V> entry : htable[index]) {
                if (entry.getKey().compareTo(key) == 0) {
                    V answer = entry.getValue();
                    entry.setValue(value);
                    return answer;
                }
            }
        } else {
            htable[index] = new ArrayList<Entry<K, V>>();
        }
        htable[index].add(new Entry<K, V>(key, value));
        ++size;
        loadFactor = (double) size / (double) capacity;
        return null;
    }
}
