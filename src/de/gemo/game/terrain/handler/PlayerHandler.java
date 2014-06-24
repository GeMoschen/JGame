package de.gemo.game.terrain.handler;

import java.util.*;

import de.gemo.game.terrain.entities.*;
import de.gemo.gameengine.units.*;

public class PlayerHandler {

    private static PlayerHandler handler;

    private List<EntityPlayer> playerList = new ArrayList<EntityPlayer>();

    public PlayerHandler() {
        handler = this;
    }

    public static void addPlayer(EntityPlayer object) {
        handler.add(object);
    }

    public static void removePlayer(EntityPlayer object) {
        handler.remove(object);
    }

    public static List<EntityPlayer> getPlayersInRadius(Vector2f center, int radius) {
        return handler.getPlayersInRange(center, radius);
    }

    public void add(EntityPlayer object) {
        this.playerList.add(object);
    }

    public void remove(EntityPlayer object) {
        for (int i = 0; i < this.playerList.size(); i++) {
            if (this.playerList.get(i) == object) {
                this.playerList.remove(i);
                return;
            }
        }
    }

    public List<EntityPlayer> getPlayersInRange(Vector2f center, int radius) {
        List<EntityPlayer> list = new ArrayList<EntityPlayer>();
        for (EntityPlayer player : this.playerList) {
            if (center.distanceTo(player.getPosition()) <= radius) {
                list.add(player);
            }
        }
        return list;
    }
}
