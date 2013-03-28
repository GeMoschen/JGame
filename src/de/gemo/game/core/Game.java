package de.gemo.game.core;

import de.gemo.engine.core.Engine;
import de.gemo.game.jbox2d.JBox2D;

public class Game extends Thread {

    public static Game INSTANCE = null;
    private final Engine engine;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (INSTANCE == null) {
            Game game = new Game(new JBox2D());
        }
    }

    public Game(Engine engine) {
        INSTANCE = this;
        this.engine = engine;
        this.engine.startUp();
        this.engine.run();
    }

    public Engine getEngine() {
        return engine;
    }
}
