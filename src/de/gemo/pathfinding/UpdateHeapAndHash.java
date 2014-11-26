package de.gemo.pathfinding;

import java.util.*;

@SuppressWarnings("rawtypes")
public class UpdateHeapAndHash<T extends Comparable<T>> extends UpdateHeap<T> {

    private Set<T> hashSet = new HashSet<T>();

    public UpdateHeapAndHash() {
        super();
    }

    public UpdateHeapAndHash(Comparator comparator) {
        super(comparator);
    }

    public UpdateHeapAndHash(int initialSize, Comparator comparator) {
        super(initialSize, comparator);
    }

    public UpdateHeapAndHash(int initialSize) {
        super(initialSize);
    }

    @Override
    public void add(T obj) {
        hashSet.add(obj);
        // TODO Auto-generated method stub
        super.add(obj);
    }

    @Override
    public boolean contains(T e) {
        return hashSet.contains(e);
    }

    @Override
    public T poll() {
        T e = super.poll();
        hashSet.remove(e);
        return e;
    }
}