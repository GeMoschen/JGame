package de.gemo.game.physics.gui.implementations;

import de.gemo.game.physics.gui.statics.GUIConfig;
import de.gemo.game.physics.gui.statics.GUITextures;
import de.gemo.gameengine.collision.Hitbox;
import de.gemo.gameengine.gui.GUIElement;
import de.gemo.gameengine.gui.GUIElementLabeled;
import de.gemo.gameengine.gui.Graphic2D;
import de.gemo.gameengine.gui.PositionAnchor;

public class GUITextfield extends GUIElementLabeled {

	public static final String configName = "Textfield";

	public GUITextfield(float x, float y, float width, float height) {
		Graphic2D left = GUIElement.createGraphic2DFromXML(GUIConfig.get(configName + ".left"), GUITextures.GUI01);
		Graphic2D center = GUIElement.createGraphic2DFromXML(GUIConfig.get(configName + ".middle"), GUITextures.GUI01);
		Graphic2D right = GUIElement.createGraphic2DFromXML(GUIConfig.get(configName + ".right"), GUITextures.GUI01);

		left.setPositionAnchor(PositionAnchor.LEFT_TOP);
		center.setPositionAnchor(PositionAnchor.LEFT_TOP);
		right.setPositionAnchor(PositionAnchor.LEFT_TOP);

		left.setSize(GUIConfig.get(configName + ".left").getWidth(), height);
		right.setSize(GUIConfig.get(configName + ".right").getHeight(), height);
		center.setSize(width - left.getWidth() - right.getWidth(), height);

		left.setPosition(0, 0);
		center.setPosition(left.getWidth(), 0);
		right.setPosition(center.getWidth() + center.getPosition().getX(), 0);

		this.addGraphic2D("left", left);
		this.addGraphic2D("center", center);
		this.addGraphic2D("right", right);

		this.setPosition(x, y);
		this.setSize(width, height);

		this.setTextAlignment(TextAlignment.CENTER);
		this.addHitbox(Hitbox.createRectangle(x + width / 2f, y + height / 2f, width / 2f, height / 2f));
	}

	public GUITextfield(float x, float y, float width, float height, String text) {
		this(x, y, width, height);
		this.setText(text);
	}

	@Override
	protected boolean onChangeSize(float oldWidth, float oldHeight, float width, float height) {
		float minWidth = GUIConfig.get(configName + ".left").getWidth() + GUIConfig.get(configName + ".right").getWidth() + 1;
		if (width < minWidth) {
			width = minWidth;
		}
		this.getGraphic2D("left").setSize(GUIConfig.get(configName + ".left").getWidth(), height);
		this.getGraphic2D("right").setSize(GUIConfig.get(configName + ".right").getWidth(), height);
		this.getGraphic2D("center").setSize(width - this.getGraphic2D("left").getWidth() - this.getGraphic2D("right").getWidth(), height);
		this.getGraphic2D("right").setPosition(this.getGraphic2D("center").getPosition().getX() + this.getGraphic2D("center").getWidth(), this.getGraphic2D("right").getPosition().getY());
		return true;
	}
}
