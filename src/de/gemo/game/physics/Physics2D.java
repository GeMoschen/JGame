package de.gemo.game.physics;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import de.gemo.game.physics.entity.EntityCollidable;
import de.gemo.game.physics.entity.Ground;
import de.gemo.game.physics.entity.Player;
import de.gemo.game.physics.entity.Wall;
import de.gemo.game.physics.gui.implementations.GUIButton;
import de.gemo.game.physics.gui.implementations.GUITextfield;
import de.gemo.game.physics.gui.implementations.TestListener;
import de.gemo.game.physics.gui.statics.GUITextures;
import de.gemo.game.physics.gui.statics.GUIXML;
import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.events.keyboard.KeyEvent;
import de.gemo.gameengine.manager.FontManager;
import de.gemo.gameengine.renderer.Renderer;

import static org.lwjgl.opengl.GL11.*;

public class Physics2D extends GameEngine {

	// 1m = 100 px;
	public static final float pxPerM = 100;

	public static World world = new World(new Vec2(0, 14.8f));

	private Player player;

	private EntityContactListener listener;
	private Vec2 screenMovement = new Vec2(0, 0);

	private long lastTimeStep = System.currentTimeMillis();

	private Vec2 upperLeft = new Vec2(0, 0);
	private Vec2 lowerRight = new Vec2(0, 0);
	private RenderCallback renderCallback;
	private List<EntityCollidable> renderList;
	private long renderTime = 0, updateTime = 0, physicsTime = 0;
	private AABB screenBounds = new AABB();

	public Physics2D(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
		super(windowTitle, windowWidth, windowHeight, 1024, 768, fullscreen);

		this.renderList = new ArrayList<EntityCollidable>();
		this.renderCallback = new RenderCallback(this.renderList);

		GUIXML.load("resources/gui.xml");
	}

	private void createBodies() {
		this.player = new Player(512, 384);
		this.screenMovement.set(0, 0);
		new Ground(1000, 550, 2000, 100, false);
		new Wall(100, -1000, 50, 3840, false, true);
		new Wall(384, -100, 25, 825, true, false);
		new Wall(1950, -1000, 50, 3840, true, false);
		for (int i = 0; i < 5; i++) {
			new Ground(384 + 300, 300 - i * 260, 600, 25, true);
		}

		listener = new EntityContactListener();
		world.setContactListener(listener);

		lastTimeStep = System.currentTimeMillis();
	}

	@Override
	protected void createManager() {
		this.createBodies();
	}

	@Override
	protected void createGUI() {
		GUITextures.load();

		// button
		GUIButton button = new GUIButton(100, 300, 100, 25);
		TestListener listener = new TestListener();
		button.setMouseListener(listener);
		button.setFocusListener(listener);
		button.setText("Button");
		this.addGUIElement("button", button);

		// textfield
		GUITextfield textfield = new GUITextfield(100, 330, 100, 25);
		textfield.setMouseListener(listener);
		textfield.setFocusListener(listener);
		textfield.setText("Textfield");
		this.addGUIElement("textfield", textfield);
	}

	@Override
	protected void tickGame(int delta) {
		this.player.tick();

		updateTime = System.currentTimeMillis();

		// prePhysics
		this.player.updatePrePhysics(delta);

		float lastX = this.player.getBody().getPosition().x;
		float lastY = this.player.getBody().getPosition().y;

		// physics
		physicsTime = System.currentTimeMillis();
		long timeNow = System.currentTimeMillis() - lastTimeStep;
		float time = (float) timeNow / 1000f;
		world.step(time, 16, 6);
		lastTimeStep = System.currentTimeMillis();
		physicsTime = lastTimeStep - physicsTime;

		// postPhysics
		this.player.updatePostPhysics(delta);

		float nowX = this.player.getBody().getPosition().x;
		float nowY = this.player.getBody().getPosition().y;

		// move screen-coordinates
		this.screenMovement.addLocal((lastX - nowX) * Physics2D.pxPerM, (lastY - nowY) * Physics2D.pxPerM);

		upperLeft.set(this.player.getPosition().x - (VIEW_WIDTH / 2f / Physics2D.pxPerM), this.player.getPosition().y - (VIEW_HEIGHT / 2f / Physics2D.pxPerM));
		lowerRight.set(upperLeft.x + (VIEW_WIDTH / Physics2D.pxPerM), upperLeft.y + (VIEW_HEIGHT / Physics2D.pxPerM));

		updateTime = System.currentTimeMillis() - updateTime;
	}

	@Override
	protected void renderGame2D() {
		renderTime = System.currentTimeMillis();

		// use JBox2D to determine if a body is on-screen
		this.renderList.clear();
		this.screenBounds = new AABB(this.upperLeft, this.lowerRight);
		world.queryAABB(this.renderCallback, screenBounds);

		// disable light
		glDisable(GL_LIGHTING);
		glDisable(GL_LIGHT0);

		glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);

		// draw background
		glPushMatrix();
		{
			glColor4f(0.2f, 0.2f, 0.5f, 1f);
			glRectf(0, 0, 1024, 768);
		}
		glPopMatrix();

		// render game
		// // render game
		// glPushMatrix();
		// {
		// glDisable(GL_TEXTURE_2D);
		// glDisable(GL_BLEND);
		//
		// // glTranslatef(0, 500, 0);
		// // glScalef(0.25f, 0.25f, 1);
		//
		// glTranslatef(screenMovement.x, screenMovement.y, 0);
		//
		// // enable depth-testing
		// glEnable(GL_DEPTH_TEST);
		// GL11.glDepthFunc(GL11.GL_LEQUAL);
		//
		// // render JBox2D-bodies
		// for (EntityCollidable entity : this.renderList) {
		// entity.
		// }
		//
		// // render SCREEN-CORNERS
		// // upper left
		// glDisable(GL_LINE_STIPPLE);
		// glLineWidth(2);
		// glPushMatrix();
		// {
		// glTranslatef(upperLeft.x * Physics2D.pxPerM, upperLeft.y *
		// Physics2D.pxPerM, 0);
		// glScalef(Physics2D.pxPerM, Physics2D.pxPerM, 0);
		// Color.green.bind();
		// glBegin(GL_LINE_LOOP);
		// glVertex2f(0f, 0f);
		// glVertex2f(lowerRight.x - upperLeft.x, 0f);
		// glVertex2f(lowerRight.x - upperLeft.x, lowerRight.y - upperLeft.y);
		// glVertex2f(0f, lowerRight.y - upperLeft.y);
		// glEnd();
		// }
		// glPopMatrix();
		// }
		// glPopMatrix();

		glPushMatrix();
		{
			glEnable(GL_TEXTURE_2D);
			glEnable(GL_BLEND);

			// this.button.renderAll();
			// this.button.renderHitbox();
			// topGraphic.renderAll();

			glDisable(GL_TEXTURE_2D);
			glDisable(GL_BLEND);
		}
		glPopMatrix();

		Renderer.renderAll();

		glPushMatrix();
		{
			// glTranslatef(0, topGraphic.getSize().y, 0f);
			glEnable(GL_TEXTURE_2D);
			glEnable(GL_BLEND);
			glDisable(GL_DEPTH_TEST);

			FontManager.getStandardFont(16, Font.BOLD).drawString(5, 5, "Prototype ( " + GameEngine.INSTANCE.getDebugMonitor().getFPS() + " fps)", Color.yellow);
			FontManager.getStandardFont().drawString(6, 30, "Left/Right to move", Color.gray);
			FontManager.getStandardFont().drawString(6, 45, "Space : jump", Color.gray);
			FontManager.getStandardFont().drawString(6, 60, "CTRL : use hook", Color.gray);
			FontManager.getStandardFont().drawString(6, 75, "R : reset", Color.gray);

			renderTime = System.currentTimeMillis() - renderTime;

			FontManager.getStandardFont().drawString(6, 90, "Rendering: " + renderTime, Color.pink);
			FontManager.getStandardFont().drawString(6, 105, "Update: " + updateTime, Color.pink);
			FontManager.getStandardFont().drawString(6, 120, "Physics: " + physicsTime, Color.pink);
			glEnable(GL_DEPTH_TEST);
		}
		glPopMatrix();
	}

	@Override
	public void onKeyPressed(KeyEvent event) {
		boolean keyHook = (event.getKey() == Keyboard.KEY_LCONTROL);
		boolean keyJump = (event.getKey() == Keyboard.KEY_SPACE);
		boolean keyLeft = (event.getKey() == Keyboard.KEY_LEFT);
		boolean keyRight = (event.getKey() == Keyboard.KEY_RIGHT);
		boolean keyUse = (event.getKey() == Keyboard.KEY_E);
		if (keyHook)
			this.player.setKeyHook(true);
		if (keyJump)
			this.player.setKeyJump(true);
		if (keyLeft)
			this.player.setKeyLeft(true);
		if (keyRight)
			this.player.setKeyRight(true);
		if (keyUse)
			this.player.setKeyUse(true);
	}

	@Override
	public void onKeyReleased(KeyEvent event) {
		boolean keyHook = (event.getKey() == Keyboard.KEY_LCONTROL);
		boolean keyJump = (event.getKey() == Keyboard.KEY_SPACE);
		boolean keyLeft = (event.getKey() == Keyboard.KEY_LEFT);
		boolean keyRight = (event.getKey() == Keyboard.KEY_RIGHT);
		boolean keyUse = (event.getKey() == Keyboard.KEY_E);
		if (keyHook)
			this.player.setKeyHook(false);
		if (keyJump)
			this.player.setKeyJump(false);
		if (keyLeft)
			this.player.setKeyLeft(false);
		if (keyRight)
			this.player.setKeyRight(false);
		if (keyUse)
			this.player.setKeyUse(false);

		if (event.getKey() == Keyboard.KEY_R) {
			this.player.setPosition(512, 384);
			this.screenMovement.set(0, 0);
		}

		if (event.getKey() == Keyboard.KEY_ESCAPE) {
			GameEngine.close();
		}
	}
}
