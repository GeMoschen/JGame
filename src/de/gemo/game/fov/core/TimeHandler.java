package de.gemo.game.fov.core;

import java.util.*;

public class TimeHandler {
    private static Map<String, Measurement> map = new HashMap<String, Measurement>();

    public static void start(String name) {
        map.put(name.toLowerCase(), new Measurement());
    }

    public static void end(String name) {
        Measurement measurement = map.get(name.toLowerCase());
        if (measurement != null) {
            measurement.stop();
            System.out.println(name + " : " + measurement.getMS() + "ms");
            map.remove(name.toLowerCase());
        }
    }
}
