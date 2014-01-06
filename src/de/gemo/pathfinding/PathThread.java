package de.gemo.pathfinding;

import java.util.*;
import java.util.concurrent.*;

public class PathThread implements Runnable {

    private Map<Integer, SinglePathSearch> queue = new HashMap<Integer, SinglePathSearch>();
    private Map<Integer, SinglePathSearch> realQueue = new ConcurrentHashMap<Integer, SinglePathSearch>();
    private Map<Integer, SinglePathSearch> ready = new ConcurrentHashMap<Integer, SinglePathSearch>();

    @Override
    public void run() {
        while (true) {

            this.realQueue.clear();
            synchronized (this.queue) {
                this.realQueue.putAll(this.queue);
                this.queue.clear();
            }

            for (Map.Entry<Integer, SinglePathSearch> entry : realQueue.entrySet()) {
                entry.getValue().run();
                ready.put(entry.getKey(), entry.getValue());
            }
            realQueue.clear();
            // try {
            // Thread.sleep(1);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
        }
    }

    public void queue(int ID, SinglePathSearch runnable) {
        synchronized (this.queue) {
            this.queue.put(ID, runnable);
        }
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
