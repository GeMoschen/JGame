package de.gemo.game.core;

public class Game {

    public static Game INSTANCE = null;

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
        new Engine();
    }

}
