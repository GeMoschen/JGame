package de.gemo.game.tile;

import java.awt.Point;
import java.util.HashSet;

public class PowerManager {
    private static HashSet<Point> powersourceTiles = new HashSet<Point>();

    public static void addPowersource(int tileX, int tileY) {
        powersourceTiles.add(new Point(tileX, tileY));
    }

    public static void removePowersource(int tileX, int tileY) {
        powersourceTiles.remove(new Point(tileX, tileY));
    }

    public static boolean isPowersource(int tileX, int tileY) {
        return powersourceTiles.contains(new Point(tileX, tileY));
    }

    public static HashSet<Point> getPowersourceTiles() {
        return powersourceTiles;
    }
}
