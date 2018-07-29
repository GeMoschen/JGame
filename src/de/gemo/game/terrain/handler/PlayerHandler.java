package de.gemo.game.terrain.handler;

import java.util.*;

import de.gemo.game.terrain.entities.*;
import de.gemo.gameengine.units.*;

public class PlayerHandler {

    private static PlayerHandler HANDLER;

    private List<EntityPlayer> _players = new ArrayList<EntityPlayer>();

    public PlayerHandler() {
        HANDLER = this;
    }

    public static void addPlayer(EntityPlayer object) {
        HANDLER.add(object);
    }

    public static void removePlayer(EntityPlayer object) {
        HANDLER.remove(object);
    }

    public static List<EntityPlayer> getPlayersInRadius(Vector2f center, int radius) {
        return HANDLER.getPlayersInRange(center, radius);
    }

    public void add(EntityPlayer object) {
        _players.add(object);
    }

    public void remove(EntityPlayer object) {
        for (int i = 0; i < _players.size(); i++) {
            if (_players.get(i) == object) {
                _players.remove(i);
                return;
            }
        }
    }

    public List<EntityPlayer> getPlayersInRange(Vector2f center, int radius) {
        List<EntityPlayer> list = new ArrayList<EntityPlayer>();
        for (EntityPlayer player : _players) {
            if (center.distanceTo(player.getPosition()) <= radius) {
                list.add(player);
            }
        }
        return list;
    }
}
