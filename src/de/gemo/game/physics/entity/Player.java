package de.gemo.game.physics.entity;

import de.gemo.game.physics.Physics2D;
import de.gemo.gameengine.manager.TextureManager;
import de.gemo.gameengine.textures.Animation;
import de.gemo.gameengine.textures.MultiTexture;
import de.gemo.gameengine.textures.SingleTexture;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.newdawn.slick.Color;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Player extends EntityCollidable {

	private boolean isJumping = false;
	private boolean doubleJumpAvailable = true;

	private boolean hookOut = false;
	private boolean hookCatched = false;
	private boolean removeHook = false;
	private boolean removeHookPartial = false;
	private boolean onGround = false;

	private boolean onWall = false;

	private Hook hook = null;

	// some fixed vars
	private Animation _animationRunning;
	private Animation _animationIdle;
	private Animation _animationOnHook;
	private Animation _animationJumping;
	private Animation _currentAnimation;
	final float jumpHeight = -5f;
	final float doubleJumpHeight = -5f;
	final float maxVelX = 10.5f;
	final float addVelX = 0.015f;

	private boolean lookingRight = false;
	private float hookDistance = 0f;

	private Vec2 currentVelocity = new Vec2(0, 0);
	private Vec2 lastPosition = new Vec2(0, 0);
	private Wall currentWall = null;

	private boolean keyJumpReleased = false, keyHookReleased = true;
	private boolean keyLeft = false, keyRight = false, keyHook = false, keyJump = false, keyUse = false;
	private long endCollision = 0;

	private int onWallTicks = 0;

	public Player(float x, float y) {
		try {
			final SingleTexture singleTexture = TextureManager.loadSingleTexture("resources/walking_animation_small.png");
			final float width = 64f;
			final float height = 64f;
			// animation idle
			{
				final MultiTexture multiTexture = new MultiTexture(width, height);
				for (int dx = 0; dx < 4; dx++) {
					multiTexture.addTextures(singleTexture.crop(width * dx, height * 0, width, height));
				}
				_animationIdle = multiTexture.toAnimation();
			}
			// animation running
			{
				final MultiTexture multiTexture = new MultiTexture(width, height);
				for (int dx = 0; dx < 8; dx++) {
					multiTexture.addTextures(singleTexture.crop(width * dx, height * 1, width, height));
				}
				_animationRunning = multiTexture.toAnimation();
			}
			// animation on hook
			{
				final MultiTexture multiTexture = new MultiTexture(width, height);
				for (int dx = 0; dx < 2; dx++) {
					multiTexture.addTextures(singleTexture.crop(width * dx, height * 2, width, height));
				}
				_animationOnHook = multiTexture.toAnimation();
			}
			// animation jumping
			{
				final MultiTexture multiTexture = new MultiTexture(width, height);
				for (int dx = 0; dx < 6; dx++) {
					multiTexture.addTextures(singleTexture.crop(width * dx, height * 3, width, height));
				}
				_animationJumping = multiTexture.toAnimation();
			}
			_currentAnimation = _animationIdle;
		} catch (IOException e) {
			e.printStackTrace();
		}
		// box
		BodyDef def = new BodyDef();
		def.type = BodyType.DYNAMIC;
		def.bullet = true;
		def.position.set(x / Physics2D.PX_PER_M, y / Physics2D.PX_PER_M);
		def.angle = 0;

		Body body = Physics2D._world.createBody(def);

		CircleShape shape = new CircleShape();
		shape.m_radius = 0.2f;

		FixtureDef fDef = new FixtureDef();
		fDef.shape = shape;
		fDef.density = 1 / 4f;
		fDef.friction = 0.2f;
		body.createFixture(fDef);

		super.init(body, x, y);
	}

	@Override
	public void updatePrePhysics(int delta) {
		if (this.getLinearVelocity().x < -maxVelX) {
			this.setLinearVelocity(-maxVelX, this.getLinearVelocity().y);
		}
		if (this.getLinearVelocity().x > maxVelX) {
			this.setLinearVelocity(maxVelX, this.getLinearVelocity().y);
		}

		this.lastPosition = this.getPosition();
		this.currentVelocity = this.getLinearVelocity();
	}

	@Override
	public void tick() {
		this.updateMovement(this.keyJump, this.keyLeft, this.keyRight);

		if (this.keyHook) {
			this.shootHook();
		} else {
			this.removeHook();
		}

		updateAnimation();
	}

	private void updateAnimation() {
		final boolean hookOn = hookCatched || hook != null && hook.hasJoint();
		if (this.onWall && !onGround) {
			_currentAnimation = _animationOnHook;
			_currentAnimation.goToFrame(1);
		} else if (isJumping && !hookOn && _currentAnimation == _animationJumping) {
			if (_currentAnimation.getCurrentStep() < 5) {
				_currentAnimation.step(8f);
			}
		} else if (onGround && Math.abs(getLinearVelocity().x) <= 1) {
			_currentAnimation = _animationIdle;
			_currentAnimation.step(4f);
		} else if (onGround && Math.abs(getLinearVelocity().x) > 1) {
			_currentAnimation = _animationRunning;
			_currentAnimation.step(Math.max(4 * (Math.abs(getLinearVelocity().x) / 4f), 1));
		} else {
			if (!onGround) {
				if (hookOn) {
					_currentAnimation = _animationOnHook;
					if (getLinearVelocity().y < -4) {
						// flying up
						_currentAnimation.setCurrentFrame(1);
					} else {
						// falling down
						_currentAnimation.setCurrentFrame(0);
					}
				} else {
					if (getLinearVelocity().y > 0) {
						_currentAnimation = _animationJumping;
						_currentAnimation.setCurrentFrame(5);
					}
				}
			} else {
				_currentAnimation.goToFrame(0);
			}
		}
	}

	@Override
	public void updatePostPhysics(int delta) {
		this._body.setAngularVelocity(0f);
		this.setAngle(0);
		if (this.hook != null && this.removeHookPartial) {
			if (this.hook.removePartial()) {
				this.setPosition(this.lastPosition);
				if (Math.abs(this.currentVelocity.x) < 2f) {
					float velX = 4f * Math.abs(this.currentVelocity.x);
					velX = Math.max(velX, 2f);
					velX = Math.min(velX, 6);
					float calcX = 0f;
					if (this.currentVelocity.x < 0) {
						calcX = -velX;
					} else if (this.currentVelocity.x > 0) {
						calcX = velX;
					}
					this.setLinearVelocity(calcX, 0);
				}
			}
		}

		if (this.removeHook) {
			if (this.hook != null) {
				this.hookOut = false;
				this.hook.destroyBody();
				this.hook = null;
				this.hookCatched = false;
				this.removeHook = false;
			}
		}

		if (this.hook != null) {
			this.hook.update(delta);
			float currentDistance = this.getDistanceSquared(this.hook);
			if (this.getLinearVelocity().y > 0.0000001f || Math.abs(currentDistance) > Math.abs(this.hookDistance)) {
				this.removeHookPartial = false;
				this._body.getPosition().set(this.lastPosition);
				this.hook.createJoint();
				this.doubleJumpAvailable = false;
			}
		}

		if (this.getLinearVelocity().x < -maxVelX) {
			this.setLinearVelocity(-maxVelX, this.getLinearVelocity().y);
		}
		if (this.getLinearVelocity().x > maxVelX) {
			this.setLinearVelocity(maxVelX, this.getLinearVelocity().y);
		}
	}

	@Override
	public void debugRender() {
		glLineWidth(1);
		Vec2 pos = this.getPosition();
		glPushMatrix();
		{
			// translate to _center
			glTranslatef(pos.x * Physics2D.PX_PER_M, pos.y * Physics2D.PX_PER_M, 10);
			glRotatef(this.getAngle(), 0, 0, 1);
			glScalef(Physics2D.PX_PER_M, Physics2D.PX_PER_M, 0);

			glDisable(GL_LINE_STIPPLE);

			// render _center
			Color.green.bind();
			glBegin(GL_LINE_LOOP);
			glVertex2f(-0.02f, -0.02f);
			glVertex2f(+0.02f, -0.02f);
			glVertex2f(+0.02f, +0.02f);
			glVertex2f(-0.02f, +0.02f);
			glEnd();

			// render boundingbox
			Color.red.bind();
			glBegin(GL_LINE_LOOP);
			final float radius = .2f;
			final AABB aabb = _body.getFixtureList().getAABB(0);
			glVertex2f(-radius, -radius);
			glVertex2f(+radius, -radius);
			glVertex2f(+radius, +radius);
			glVertex2f(-radius, +radius);
			glEnd();

			float offX = 0.1f;
			if (!lookingRight) {
				offX = -0.1f;
			}

			// render lookDirection
			Color.pink.bind();
			glBegin(GL_LINE_LOOP);
			glVertex2f(0, 0);
			glVertex2f(offX, 0);
			glEnd();

		}
		glPopMatrix();
	}

	private void debugRenderHook() {
		// render hook
		if (this.hook != null) {
			glPushMatrix();
			{
				Vec2 pos = this.getPosition();
				// translate to _center
				glTranslatef(pos.x * Physics2D.PX_PER_M, pos.y * Physics2D.PX_PER_M, 8);
				glScalef(Physics2D.PX_PER_M, Physics2D.PX_PER_M, 0);

				glDisable(GL_LINE_STIPPLE);

				// render _center
				Color.white.bind();
				glBegin(GL_LINES);
				glVertex2f(0, 0);
				glVertex2f(this.hook.getPosition().x - this.getPosition().x, this.hook.getPosition().y - this.getPosition().y);
				glEnd();
			}
			glPopMatrix();

			this.hook.render();
		}
	}

	@Override
	public void render() {
		debugRenderHook();
		glPushMatrix();
		{
			Vec2 pos = this.getPosition();
			// translate to _center
			glTranslatef(pos.x * Physics2D.PX_PER_M, (pos.y - 0.05f) * Physics2D.PX_PER_M, 10);
			if (!lookingRight) {
				glScalef(-1, 1, 1);
			}
			if (Physics2D.SELECTED == getBody()) {
				_currentAnimation.render(1f, 0f, 0f, 1);
			} else {
				_currentAnimation.render();
			}
		}
		glPopMatrix();
		this.debugRender();
	}

	protected void removeHook() {
		if (this.hook != null) {
			this.removeHook = true;
			this.keyJumpReleased = false;
		}
	}

	private void shootHook() {
		if (!this.hookOut && this.hook == null && this.keyHookReleased) {
			this.removeHookPartial = false;
			this.keyHookReleased = false;
			this.removeHook = false;
			this.hookCatched = false;
			this.hookOut = true;
			this.hook = new Hook(this, this._body.getPosition().x * Physics2D.PX_PER_M, this._body.getPosition().y * Physics2D.PX_PER_M, 0, !this.lookingRight);
		}
	}

	private void updateMovement(boolean spaceDown, boolean leftDown, boolean rightDown) {
		if (keyJumpReleased && isJumping && spaceDown && doubleJumpAvailable) {
			// double jump
			isJumping = true;
			this.setLinearVelocity(this.getLinearVelocity().x, doubleJumpHeight);
			doubleJumpAvailable = false;
			// update animation
			_currentAnimation = _animationJumping;
			_animationJumping.setCurrentFrame(0);
		} else if (spaceDown && !isJumping) {
			// jump on wall
			if (this.onWall) {
				float speedX = 0f;
				if (this.currentWall.isLeft()) {
					speedX = -0.15f;
					this.lookingRight = false;
				} else if (currentWall.isRight()) {
					speedX = +0.15f;
					this.lookingRight = true;
				}
				if (speedX != 0f) {
					this._body.applyLinearImpulse(new Vec2(speedX, -0.05f), this._body.getPosition());
					this.onWall = false;
					this.onGround = false;
					this.isJumping = true;
					_currentAnimation = _animationJumping;
					_currentAnimation.setCurrentFrame(0);
				}
			} else {
				// normal jump
				isJumping = true;
				this.setLinearVelocity(this.getLinearVelocity().x, jumpHeight);
				this.doubleJumpAvailable = true;
				this.onGround = false;
				this.keyJumpReleased = false;

				// update animation
				_currentAnimation = _animationJumping;
				_animationJumping.setCurrentFrame(0);
			}
		}

		// move left
		if (leftDown && !rightDown) {
			if (this._body.getLinearVelocity().x > -maxVelX) {
				if (this.onWall) {
					this.onWallTicks++;
				} else {
					this.onWallTicks = 0;
				}
				float half = 0.5f;
				if (this.getLinearVelocity().x > 0) {
					half = 0.5f;
				} else if (this.hookCatched && !this.onGround && !this.onWall) {
					half = 1.8f;
				} else if (!this.onGround) {
					half = 0.5f;
				} else if (this.hookCatched) {
					half = 0.8f;
				} else if (this.onGround && this.getLinearVelocity().x <= 0) {
					half = 1;
				}

				if (onWall && !onGround)
					half = 0.3f;

				if (this.onWallTicks > 10 || !this.onWall) {
					this._body.applyLinearImpulse(new Vec2(half * (-addVelX), 0), this._body.getPosition());
					this.onWallTicks = 0;
				}
			}
			if (!this.onWall) {
				this.lookingRight = false;
			}
		}

		// move right
		if (!leftDown && rightDown) {
			if (this._body.getLinearVelocity().x < maxVelX) {
				if (this.onWall) {
					this.onWallTicks++;
				} else {
					this.onWallTicks = 0;
				}
				float half = 0.5f;
				if (this.getLinearVelocity().x < 0) {
					half = 0.5f;
				} else if (this.hookCatched && !this.onGround && !this.onWall) {
					half = 1.8f;
				} else if (!this.onGround) {
					half = 0.5f;
				} else if (this.hookCatched) {
					half = 0.8f;
				} else if (this.onGround && this.getLinearVelocity().x >= 0) {
					half = 1;
				}
				if (onWall && !onGround)
					half = 0.3f;

				if (this.onWallTicks > 10 || !this.onWall) {
					this.onWallTicks = 0;
					this._body.applyLinearImpulse(new Vec2(half * (+addVelX), 0), this.getPosition());
				}
			}
			if (!this.onWall) {
				this.lookingRight = true;
			}
		}
	}

	private void setDoubleJumpAvailable(boolean doubleJumpAvailable) {
		this.doubleJumpAvailable = doubleJumpAvailable;
	}

	public void setJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	@Override
	public boolean beginCollision(EntityCollidable entity, Contact contact) {
		if (entity instanceof Ground) {
			return this.hitGround((Ground) entity, contact.getManifold().localPoint);
		}

		if (entity instanceof Wall) {
			this.hitWall((Wall) entity, contact.getManifold().localPoint);
			return true;
		}

		// ignore hooks
		if (entity instanceof Hook) {
			return false;
		}

		// ignore players
		if (entity instanceof Player) {
			return false;
		}
		return true;
	}

	@Override
	public boolean endCollision(EntityCollidable entity, Contact contact) {
		if (entity instanceof Ground) {
			this.leftGround((Ground) entity, contact.getManifold().localPoint);
			return true;
		}
		if (entity instanceof Wall) {
			this.leftWall((Wall) entity, contact.getManifold().localPoint);
			return true;
		}
		return true;
	}

	private boolean hitGround(Ground groundBox, Vec2 hitPoint) {
		this.setJumping(false);
		this.setDoubleJumpAvailable(true);

		int yPos2 = (int) (groundBox.getHalfHeight() * -10);
		int yPos3 = (int) (hitPoint.y * 10 * Physics2D.PX_PER_M);
		this.onGround = (yPos2 == yPos3);

		if (this.onGround) {
			this.onWall = false;
			this.removeHookPartial();
			return true;
		}
		return false;
	}

	private void removeHookPartial() {
		if (!this.removeHookPartial) {
			this.hookCatched = false;
			this.removeHookPartial = true;
		}
	}

	private void hitWall(Wall wall, Vec2 hitPoint) {
		Vec2 vel = this._body.getLinearVelocity();
		if ((vel.x <= 0 && wall.isRight())) {
			int yPos2 = (int) (wall.getHalfWidth() * 10);
			int yPos3 = (int) (hitPoint.x * 10 * Physics2D.PX_PER_M);
			this.onWall = (yPos2 == yPos3);
			if (this.onWall) {
				this.onGround = false;
				this.currentWall = wall;
			} else {
				this.currentWall = null;
			}
		} else if ((vel.x >= 0 && wall.isLeft())) {
			int yPos2 = (int) (wall.getHalfWidth() * -10);
			int yPos3 = (int) (hitPoint.x * 10 * Physics2D.PX_PER_M);
			this.onWall = (yPos2 == yPos3);
			if (this.onWall) {
				this.onGround = false;
				this.currentWall = wall;
			} else {
				this.currentWall = null;
			}
		} else {
			this.onWall = false;
			this.currentWall = null;
		}

		float velo = (float) Math.abs(vel.x * 0.77f);
		if (this.onWall) {
			this.onWallTicks = 0;
			if (vel.y <= 0f) {
				velo = Math.min(velo, 6.5f);
				velo = Math.max(velo, 0.43f);
			}

			this.lookingRight = !this.lookingRight;
			this.setJumping(false);
			this.setDoubleJumpAvailable(true);
		}
		if (this.onWall && vel.y <= 0f) {
			this.setLinearVelocity(0, -velo);
		}

		if (this.hook != null && this.hookCatched) {
			this.removeHook();
		}
	}

	private void leftGround(Ground ground, Vec2 hitPoint) {
		this.onGround = false;
	}

	private void leftWall(Wall wall, Vec2 hitPoint) {
		this.onWall = false;
	}

	public boolean isHookCatched() {
		return hookCatched;
	}

	public void setHookCatched(boolean hookCatched) {
		this.hookCatched = hookCatched;
	}

	public void setHookDistance(float hookDistance) {
		this.hookDistance = hookDistance;
	}

	public void setKeyLeft(boolean keyLeft) {
		this.keyLeft = keyLeft;
	}

	public void setKeyRight(boolean keyRight) {
		this.keyRight = keyRight;
	}

	public void setKeyHook(boolean keyHook) {
		this.keyHook = keyHook;
		this.keyHookReleased = true;
	}

	public void setKeyJump(boolean keyJump) {
		if (!keyJump) {
			this.keyJumpReleased = true;
		}
		this.keyJump = keyJump;
	}

	public void setKeyUse(boolean keyUse) {
		this.keyUse = keyUse;
	}

}
