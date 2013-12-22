package de.gemo.pathfinding;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class UpdateHeap<T extends Comparable<T>> {

    private T[] elements;
    private int n = 0;

    public static final Comparator MAX_HEAP = new Comparator() {
        public int compare(Object o1, Object o2) {
            return ((Comparable) o1).compareTo(o2);
        }
    };

    public static final Comparator MIN_HEAP = Collections.reverseOrder(MAX_HEAP);

    private static final int MIN_SIZE = 16;

    private Comparator<T> comparator;

    public UpdateHeap() {
        this(MIN_SIZE, MIN_HEAP);
    }

    public UpdateHeap(Comparator comparator) {
        this(MIN_SIZE, comparator);
    }

    public UpdateHeap(int initialSize) {
        this(initialSize, MIN_HEAP);
    }

    public UpdateHeap(int initialSize, Comparator comparator) {
        this.elements = (T[]) Array.newInstance(Comparable.class, initialSize + 1);
        this.comparator = comparator;
    }

    public int getSize() {
        return n;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    private void swap(int i, int j) {
        T tmp = elements[i];
        elements[i] = elements[j];
        elements[j] = tmp;
    }

    private boolean isLess(int i, int j) {
        return comparator.compare(elements[i], elements[j]) < 0;
    }

    public T poll() {

        T e = elements[1];
        swap(1, n);
        elements[n--] = null;
        sink(1);
        return e;
    }

    public void add(T obj) {
        n++;
        if (n == elements.length)
            resize(n * 2);
        elements[n] = obj;

        swim(n);
    }

    private void resize(int newSize) {
        this.elements = Arrays.copyOf(elements, newSize + 1);
    }

    public void swim(int k) {

        while (k > 1 && isLess(k / 2, k)) {
            swap(k / 2, k);
            k = k / 2;
        }
    }

    public void sink(int k) {

        int j = 0;
        while (k * 2 <= n) {
            j = k * 2;
            if (j < n && isLess(j, j + 1))
                ++j;
            if (!isLess(k, j))
                break;
            swap(k, j);
            k = j;
        }
    }

    public void clear() {
        UpdateHeap<T> tmp = new UpdateHeap<T>(MIN_SIZE, comparator);
        this.elements = tmp.elements;
        this.n = tmp.n;
    }

    public boolean update(T e) {
        for (int i = 1; i < n; ++i) {
            T tmp = elements[i];
            if (tmp == e) {
                if (isLess(i, i / 2))
                    swim(i);
                else
                    sink(i);

                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder sBuilder = new StringBuilder("[");
        int i = 1;
        for (; i < n; ++i) {
            sBuilder.append(elements[i]).append(", ");
        }
        sBuilder.append(elements[i]).append(']');
        return sBuilder.toString();
    }

    public boolean contains(T e) {
        for (int i = 1; i <= n; ++i) {
            if (elements[i].equals(e))
                return true;
        }
        return false;
    }

    public static void main(String[] args) {

        UpdateHeap<Character> heap = new UpdateHeap<Character>(MAX_HEAP);
        Character[] chars = { 'X', 'T', 'O', 'G', 'S', 'M', 'N', 'A', 'E', 'R', 'A', 'I' };
        for (Character c : chars)
            heap.add(c);

        System.out.println(heap);
        heap.poll();
        System.out.println(heap);

        System.out.println();

        // CONTROLL UNIT - PRIORITYQUEUE IS AN IMPLEMENTATION OF A MIN HEAP
        PriorityQueue<Character> queue2 = new PriorityQueue<Character>(16, Collections.reverseOrder());
        for (Character c : chars)
            queue2.add(c);

        System.out.println(queue2);
        queue2.poll();
        System.out.println(queue2);

        if (queue2.toString().equals(heap.toString())) {
            System.out.println("Heap is correct");
        }
    }
}