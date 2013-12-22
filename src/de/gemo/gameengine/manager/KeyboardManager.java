package de.gemo.gameengine.manager;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.events.keyboard.KeyEvent;

public class KeyboardManager {
	public static KeyboardManager INSTANCE = null;

	private final GameEngine engine;
	public HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Character> holdKeys = new HashMap<Integer, Character>();

	public static KeyboardManager getInstance(GameEngine engine) {
		if (INSTANCE == null) {
			return new KeyboardManager(engine);
		} else {
			throw new RuntimeException("ERROR: KeyManager is already created!");
		}
	}

	private KeyboardManager(GameEngine engine) {
		INSTANCE = this;
		this.engine = engine;
		this.holdKeys = new HashMap<Integer, Character>();
		for (int index = 0; index < 65536; index++) {
			pressedKeys.put(index, false);
		}
		Keyboard.enableRepeatEvents(false);
	}

	public void update() {
		// iterate over currently pressed keys to handle hold keys
		for (Map.Entry<Integer, Character> entry : this.holdKeys.entrySet()) {
			if (Keyboard.isKeyDown(entry.getKey())) {
				// hold key
				engine.onKeyHold(new KeyEvent(entry.getKey(), entry.getValue(), true));
			}
		}

		while (Keyboard.next()) {
			boolean currentState = Keyboard.getEventKeyState();
			char character = Keyboard.getEventCharacter();
			int key = Keyboard.getEventKey();
			boolean oldState = holdKeys.containsKey(key);

			if (!currentState && oldState) {
				// released key
				engine.onKeyReleased(new KeyEvent(key, holdKeys.remove(key), false));
			}
			if (currentState && !oldState) {
				// newly pressed key
				engine.onKeyPressed(new KeyEvent(key, character, true));
				holdKeys.put(key, character);
			}
			pressedKeys.put(key, currentState);
		}
	}

	public boolean isKeyDown(int key) {
		return pressedKeys.get(key);
	}
}
