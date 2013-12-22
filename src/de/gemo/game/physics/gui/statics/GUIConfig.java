package de.gemo.game.physics.gui.statics;

import java.util.HashMap;
import java.util.Map;

public class GUIConfig {

	public static class GUIElementConfig {
		private final float x, y;
		private final float width, height;

		private GUIElementConfig(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public float getWidth() {
			return width;
		}

		public float getHeight() {
			return height;
		}
	}

	private static Map<String, GUIElementConfig> configList = new HashMap<String, GUIElementConfig>();

	public static void add(String name, GUIElementConfig config) {
		configList.put(name, config);
	}

	public static GUIElementConfig get(String name) {
		return configList.get(name);
	}

	private static GUIElementConfig load(String xmlPath) {
		GUIElementConfig config = new GUIElementConfig(GUIXML.getInt(xmlPath + ".X", -1), GUIXML.getInt(xmlPath + ".Y", -1), GUIXML.getInt(xmlPath + ".Width", 0), GUIXML.getInt(xmlPath + ".Height", 0));
		return config;
	}

	static {
		// Buttons
		add("Button.left", load("Element.Button.Left"));
		add("Button.middle", load("Element.Button.Middle"));
		add("Button.right", load("Element.Button.Right"));

		// Textfields
		add("Textfield.left", load("Element.Textfield.Left"));
		add("Textfield.middle", load("Element.Textfield.Middle"));
		add("Textfield.right", load("Element.Textfield.Right"));
	}

}
