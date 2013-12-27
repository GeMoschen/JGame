package de.gemo.pathfinding;

import java.util.*;
import java.util.concurrent.*;

public class PathThread implements Runnable {

    private Map<Integer, SinglePathSearch> queue = new ConcurrentHashMap<Integer, SinglePathSearch>();
    private Map<Integer, SinglePathSearch> ready = new ConcurrentHashMap<Integer, SinglePathSearch>();

    @Override
    public void run() {
        while (true) {
            for (Map.Entry<Integer, SinglePathSearch> entry : queue.entrySet()) {
                entry.getValue().run();
                ready.put(entry.getKey(), entry.getValue());
            }
            queue.clear();
        }
    }

    public void queue(int ID, SinglePathSearch runnable) {
        this.queue.put(ID, runnable);
    }

    public boolean isReady(int ID) {
        SinglePathSearch runnable = this.poll(ID);
        if (runnable != null) {
            return runnable.isSearchDone();
        }
        return false;
    }

    public SinglePathSearch poll(int ID) {
        SinglePathSearch runnable = this.ready.get(ID);
        if (runnable != null) {
            this.ready.remove(ID);
            return runnable;
        }
        return null;
    }

}
