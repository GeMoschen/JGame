package de.gemo.game.tile.manager;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import de.gemo.game.tile.IsoMap;

public class HouseManager {
    private static Random generator = new Random();
    private static ArrayList<Point> arrayList = new ArrayList<Point>();
    private static HashSet<Point> houseTiles = new HashSet<Point>();

    public static void addHouse(int tileX, int tileY) {
        houseTiles.add(new Point(tileX, tileY));
        HouseManager.updateList();
    }

    public static void removeHouse(int tileX, int tileY) {
        houseTiles.remove(new Point(tileX, tileY));
        HouseManager.updateList();
    }

    public static boolean isHouse(int tileX, int tileY) {
        return houseTiles.contains(new Point(tileX, tileY));
    }

    public static HashSet<Point> getHouseTiles() {
        return houseTiles;
    }

    public static void clearAll() {
        houseTiles.clear();
    }

    public static void updateList() {
        arrayList = new ArrayList<Point>(houseTiles);
    }

    public static void doRandomTicks(IsoMap isoMap, int updates) {
        int index;
        Point point;
        HouseManager.updateList();
        for (int i = 0; i < updates && arrayList.size() > 0; i++) {
            index = generator.nextInt(arrayList.size());
            point = arrayList.get(index);
            isoMap.getTile(point.x, point.y).doTick(isoMap, point.x, point.y);
            if (index >= 0 && index < arrayList.size()) {
                arrayList.remove(index);
            }
        }
    }
}
