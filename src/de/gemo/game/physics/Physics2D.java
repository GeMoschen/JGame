package de.gemo.game.physics;

import de.gemo.game.physics.entity.*;
import de.gemo.game.physics.gui.statics.GUIXML;
import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.events.keyboard.KeyEvent;
import de.gemo.gameengine.events.mouse.MouseClickEvent;
import de.gemo.gameengine.events.mouse.MouseDragEvent;
import de.gemo.gameengine.manager.FontManager;
import de.gemo.gameengine.manager.TextureManager;
import de.gemo.gameengine.renderer.Renderer;
import de.gemo.gameengine.textures.SingleTexture;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Physics2D extends GameEngine {

	public static Body SELECTED = null;
	private static Fixture SELECTED_FIXTURE = null;
	private static final List<Fixture> SELECTED_FIXTURES = new ArrayList<>();

	// 1m = 100 px;
	public static final float PX_PER_M = 100f;

	public static World _world = new World(new Vec2(0, 14.8f));

	private Player _player;

	private EntityContactListener _entityContactListener;
	private Vec2 _screenMovement = new Vec2(0, 0);

	private long _lastTimeStep = System.currentTimeMillis();

	private Vec2 _lowerScreenBound = new Vec2(0, 0);
	private Vec2 _upperScreenBound = new Vec2(0, 0);
	private RenderCallback _renderCallback;
	private long _renderTime = 0, _updateTime = 0, _physicsTime = 0;
	private AABB _screenBounds = new AABB();
	private SingleTexture _backgroundTexture;

	public Physics2D(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
		super(windowTitle, windowWidth, windowHeight, 1024, 768, fullscreen);

		_renderCallback = new RenderCallback();

		GUIXML.load("resources/gui.xml");
	}

	private void createBodies() {
		_player = new Player(512, 384);
		_screenMovement.set(0, 0);
		new Wall(100, -1000, 50, 3840, false, true);
		new Wall(384, -100, 25, 825, true, false);
		new Wall(1950, -1000, 50, 3840, true, false);
		new Ground(1000, 550, 2000, 100, false);
		for (int i = 0; i < 5; i++) {
			new Ground(384 + 300 + 150 * i, 300 - i * 260, 600, 25, true);
		}

		_entityContactListener = new EntityContactListener();
		_world.setContactListener(_entityContactListener);

		_lastTimeStep = System.currentTimeMillis();
	}

	@Override
	protected void createManager() {
		try {
			_backgroundTexture = TextureManager.loadSingleTexture("resources/background_speedy.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		createBodies();
	}

	@Override
	protected void createGUI() {
	}

	@Override
	protected void tickGame(int delta) {
		_player.tick();

		_updateTime = System.currentTimeMillis();

		// prePhysics
		_player.updatePrePhysics(delta);

		float lastX = _player.getBody().getPosition().x;
		float lastY = _player.getBody().getPosition().y;

		// physics
		_physicsTime = System.currentTimeMillis();
		final float time = 1f / 60f;
		_world.step(time, 16, 6);
		_lastTimeStep = System.currentTimeMillis();
		_physicsTime = _lastTimeStep - _physicsTime;

		// postPhysics
		_player.updatePostPhysics(delta);

		float nowX = _player.getBody().getPosition().x;
		float nowY = _player.getBody().getPosition().y;

		// move screen-coordinates
		_screenMovement.addLocal((lastX - nowX) * Physics2D.PX_PER_M, (lastY - nowY) * Physics2D.PX_PER_M);

		_lowerScreenBound.set(_player.getPosition().x - (VIEW_WIDTH / 2f / Physics2D.PX_PER_M), _player.getPosition().y - (VIEW_HEIGHT / 2f / Physics2D.PX_PER_M));
		_upperScreenBound.set(_lowerScreenBound.x + (VIEW_WIDTH / Physics2D.PX_PER_M), _lowerScreenBound.y + (VIEW_HEIGHT / Physics2D.PX_PER_M));

		_updateTime = System.currentTimeMillis() - _updateTime;
	}

	@Override
	protected void renderGame2D() {
		_renderTime = System.currentTimeMillis();
		updateEntitiesOnScreen();

		// render game
		glPushMatrix();
		{
			renderLevelBackground();
			glTranslatef(_screenMovement.x, _screenMovement.y, 0);
			renderEntities();
			renderScreenBorder();
		}
		glPopMatrix();

		renderUi();
	}

	private void renderLevelBackground() {
		glPushMatrix();
		{
			glTranslatef(getWindowWidth() / 2, getWindowHeight() / 2, 0);
			_backgroundTexture.render(1, 1, 1, 1);
		}
		glPopMatrix();
	}

	private void renderEntities() {
		final List<EntityCollidable> onScreenRenderList = _renderCallback.getRenderList();
		for (EntityCollidable entity : onScreenRenderList) {
			entity.render();
		}
	}

	private void renderUi() {
		Renderer.renderAll();

		glPushMatrix();
		{
			// glTranslatef(0, topGraphic.getSize().y, 0f);
			glEnable(GL_TEXTURE_2D);
			glEnable(GL_BLEND);
			glDisable(GL_DEPTH_TEST);

			FontManager.getStandardFont(16, Font.BOLD).drawString(5, 5, "Prototype ( " + GameEngine.$.getDebugMonitor().getFPS() + " fps)", Color.yellow);
			FontManager.getStandardFont().drawString(6, 30, "Left/Right to move", Color.gray);
			FontManager.getStandardFont().drawString(6, 45, "Space : jump", Color.gray);
			FontManager.getStandardFont().drawString(6, 60, "CTRL : use hook", Color.gray);
			FontManager.getStandardFont().drawString(6, 75, "R : reset", Color.gray);

			_renderTime = System.currentTimeMillis() - _renderTime;

			FontManager.getStandardFont().drawString(6, 90, "Rendering: " + _renderTime, Color.pink);
			FontManager.getStandardFont().drawString(6, 105, "Update: " + _updateTime, Color.pink);
			FontManager.getStandardFont().drawString(6, 120, "Physics: " + _physicsTime, Color.pink);
			glEnable(GL_DEPTH_TEST);
		}
		glPopMatrix();
	}

	private void renderScreenBorder() {
		// upper left
		glDisable(GL_LINE_STIPPLE);
		glLineWidth(2);
		glPushMatrix();
		{
			glTranslatef(_lowerScreenBound.x * Physics2D.PX_PER_M, _lowerScreenBound.y * Physics2D.PX_PER_M, 0);
			glScalef(Physics2D.PX_PER_M, Physics2D.PX_PER_M, 0);
			Color.green.bind();
			glBegin(GL_LINE_LOOP);
			glVertex2f(0f, 0f);
			glVertex2f(_upperScreenBound.x - _lowerScreenBound.x, 0f);
			glVertex2f(_upperScreenBound.x - _lowerScreenBound.x, _upperScreenBound.y - _lowerScreenBound.y);
			glVertex2f(0f, _upperScreenBound.y - _lowerScreenBound.y);
			glEnd();
		}
		glPopMatrix();
	}

	private void updateEntitiesOnScreen() {
		// update screen boundaries
		if (_screenBounds == null) {
			_screenBounds = new AABB(_lowerScreenBound, _upperScreenBound);
		} else {
			_screenBounds.lowerBound.set(_lowerScreenBound);
			_screenBounds.upperBound.set(_upperScreenBound);
		}

		// use JBox2D to determine if a body is on-screen
		_renderCallback.reset();
		checkAABB(_renderCallback, _screenBounds);
	}

	@Override
	public void onKeyPressed(KeyEvent event) {
		boolean keyHook = (event.getKey() == Keyboard.KEY_LCONTROL);
		boolean keyJump = (event.getKey() == Keyboard.KEY_SPACE);
		boolean keyLeft = (event.getKey() == Keyboard.KEY_LEFT);
		boolean keyRight = (event.getKey() == Keyboard.KEY_RIGHT);
		boolean keyUse = (event.getKey() == Keyboard.KEY_E);
		if (keyHook)
			_player.setKeyHook(true);
		if (keyJump)
			_player.setKeyJump(true);
		if (keyLeft)
			_player.setKeyLeft(true);
		if (keyRight)
			_player.setKeyRight(true);
		if (keyUse)
			_player.setKeyUse(true);

		super.onKeyPressed(event);
	}

	@Override
	public void onKeyReleased(KeyEvent event) {
		boolean keyHook = (event.getKey() == Keyboard.KEY_LCONTROL);
		boolean keyJump = (event.getKey() == Keyboard.KEY_SPACE);
		boolean keyLeft = (event.getKey() == Keyboard.KEY_LEFT);
		boolean keyRight = (event.getKey() == Keyboard.KEY_RIGHT);
		boolean keyUse = (event.getKey() == Keyboard.KEY_E);
		if (keyHook)
			_player.setKeyHook(false);
		if (keyJump)
			_player.setKeyJump(false);
		if (keyLeft)
			_player.setKeyLeft(false);
		if (keyRight)
			_player.setKeyRight(false);
		if (keyUse)
			_player.setKeyUse(false);

		if (event.getKey() == Keyboard.KEY_R) {
			_player.setPosition(512, 384);
			_screenMovement.set(0, 0);
		}

		if (event.getKey() == Keyboard.KEY_ESCAPE) {
			GameEngine.close();
		}

		super.onKeyReleased(event);
	}

	@Override
	public void onMouseDown(final boolean handled, final MouseClickEvent event) {
		if (event.isLeftButton()) {
			final QueryCallback queryCallback = fixture -> {
				SELECTED_FIXTURES.add(fixture);
				return true;
			};
			SELECTED = null;
			SELECTED_FIXTURE = null;
			final AABB aabb = new AABB();
			final float factor = 1 / PX_PER_M;
			final float x = (getMouseManager().getCurrentX() - _screenMovement.x) * factor;
			final float y = (getMouseManager().getCurrentY() - _screenMovement.y) * factor;
			aabb.lowerBound.x = x;
			aabb.lowerBound.y = y;
			aabb.upperBound.x = aabb.lowerBound.x;
			aabb.upperBound.y = aabb.lowerBound.y;
			SELECTED_FIXTURES.clear();
			checkAABB(queryCallback, aabb);

			// check
			for (final Fixture fixture : SELECTED_FIXTURES) {
				final Body body = fixture.getBody();
				final Object fixtureUserData = fixture.getUserData();
				if (fixtureUserData instanceof DelegatingUserData) {
					final DelegatingUserData delegatingUserData = DelegatingUserData.class.cast(fixtureUserData);
					SELECTED = fixture.getBody();
					SELECTED_FIXTURE = fixture;
					break;
				}
				final Object userData = body.getUserData();
				if (userData instanceof EntityCollidable) {
					SELECTED = body;
					SELECTED_FIXTURE = fixture;
				}
			}
		}
	}

	@Override
	public void onMouseDrag(final boolean handled, final MouseDragEvent event) {
		final Fixture fixture = SELECTED_FIXTURE;
		if (fixture != null && fixture.getUserData() instanceof DelegatingUserData) {
			final DelegatingUserData userData = (DelegatingUserData) fixture.getUserData();
			if (userData.getType() == DelegatingUserData.Type.GRAB && userData.getEntity() instanceof GrabberHolder) {
				final GrabberHolder grabberHolder = (GrabberHolder) userData.getEntity();
				grabberHolder.moveGrabber(event.getDifX() / PX_PER_M, event.getDifY() / PX_PER_M, userData.getUserData() );
			}
		}
	}

	private void checkAABB(final QueryCallback callback, final AABB aabb) {
		Body body = _world.getBodyList();
		while (body != null) {
			Fixture fixture = body.getFixtureList();
			while (fixture != null) {
				final int proxyCount = fixture.m_proxyCount;
				for (int i = 0; i < proxyCount; i++) {
					final AABB otherAABB = fixture.getAABB(i);
					if (AABB.testOverlap(aabb, otherAABB)) {
						if (!callback.reportFixture(fixture)) {
							return;
						}
					}
				}
				fixture = fixture.getNext();
			}
			body = body.getNext();
		}
	}

}
