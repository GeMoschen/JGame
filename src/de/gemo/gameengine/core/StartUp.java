package de.gemo.gameengine.core;

import de.gemo.game.physics.GLSL;

public class StartUp extends Thread {

	public static StartUp INSTANCE = null;
	private final GameEngine gameEngine;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (INSTANCE == null) {
			// StartUp game =
			new StartUp(new GLSL("Shadertest", 1024, 768, false));
		}
	}

	public StartUp(GameEngine engine) {
		INSTANCE = this;
		this.gameEngine = engine;
		this.gameEngine.startUp();
		this.gameEngine.run();
	}

	public GameEngine getEngine() {
		return gameEngine;
	}
}
