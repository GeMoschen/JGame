package de.gemo.game.core;

import de.gemo.engine.core.Engine;

public class Game {

    public static Game INSTANCE = null;
    private final Engine engine;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (INSTANCE == null) {
            new Game();
        }
    }

    public Game() {
        INSTANCE = this;
        this.engine = new Minetown();
        this.engine.startUp();
    }

    public Engine getEngine() {
        return engine;
    }

}
