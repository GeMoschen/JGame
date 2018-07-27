package de.gemo.gameengine.core;

import de.gemo.game.physics.Physics2D;
import de.gemo.game.polygon.core.PolygonCore;

public class StartUp extends Thread {

    public static StartUp $ = null;
    private final GameEngine gameEngine;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if ($ == null) {
            // StartUp game =
            new StartUp(new Physics2D("3D-Collision", 1024, 768, false));
        }
    }

    public StartUp(GameEngine engine) {
        $ = this;
        this.gameEngine = engine;
        this.gameEngine.startUp();
        this.gameEngine.run();
    }

    public GameEngine getEngine() {
        return gameEngine;
    }
}
