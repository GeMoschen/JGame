package de.gemo.game.terrain.entities;

import de.gemo.game.terrain.core.TerrainCore;
import de.gemo.game.terrain.entities.weapons.EntityBazooka;
import de.gemo.game.terrain.handler.PlayerHandler;
import de.gemo.game.terrain.world.World;
import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.manager.FontManager;
import de.gemo.gameengine.manager.TextureManager;
import de.gemo.gameengine.textures.Animation;
import de.gemo.gameengine.textures.MultiTexture;
import de.gemo.gameengine.textures.SingleTexture;
import de.gemo.gameengine.units.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.opengl.TextureImpl;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class EntityPlayer implements IPhysicsObject, IRenderObject {

    private static SingleTexture CROSSHAIR;
    private static Animation ANIMATION_WALK = null;
    private static Animation ANIMATION_JUMP = null;

    static {
        try {
            {
                final SingleTexture singleTexture = TextureManager.loadSingleTexture("resources/worms/walk.png", GL_LINEAR);
                final int dim = 64;
                final MultiTexture multiTexture = new MultiTexture(dim, dim);
                for (int y = 0; y < dim * 15; y += dim) {
                    for (int x = 0; x < dim; x += dim) {
                        multiTexture.addTextures(singleTexture.crop(x, y, dim, dim));
                    }
                }
                ANIMATION_WALK = multiTexture.toAnimation();
            }
            {
                final SingleTexture singleTexture = TextureManager.loadSingleTexture("resources/worms/jump.png", GL_LINEAR);
                final int dim = 64;
                final MultiTexture multiTexture = new MultiTexture(dim, dim);
                for (int x = 0; x < dim * 8; x += dim) {
                    multiTexture.addTextures(singleTexture.crop(x, 0, dim, dim));
                }
                ANIMATION_JUMP = multiTexture.toAnimation();
            }
            CROSSHAIR = TextureManager.loadSingleTexture("resources/crosshair.png", GL_LINEAR);
        } catch (
                final IOException e)

        {
            e.printStackTrace();
        }

    }

    private Animation _animation;
    private int _playerWidth = 5, _playerHeight = 10;
    private Vector2f _position, _velocity;
    private boolean _lookRight = true;
    private World _world;

    private float _shootAngle = 0f;
    private float _shootPower = 0f;

    private boolean[] _movement = new boolean[5];
    private boolean _onGround, _shotFired = false, _pushedByWeapon = false;
    private boolean _jumping = false;

    private Class<? extends EntityWeapon> currentWeapon = EntityBazooka.class;

    private int _health = 100;
    private final int _teamId;

    private final static int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, SPACE = 4;

    public EntityPlayer(World world, Vector2f position, int teamId) {
        _animation = ANIMATION_WALK.clone();
        _teamId = teamId;
        _world = world;
        _position = position.clone();
        _velocity = new Vector2f(0, 0);
        PlayerHandler.addPlayer(this);
    }

    public EntityPlayer(World world, float x, float y, int teamId) {
        this(world, new Vector2f(x, y), teamId);
    }

    public void jump() {
        if ((_onGround)) {
            _velocity.setY(-0.2f * GameEngine.$.getCurrentDelta());
            float jumpX = 0.1f * GameEngine.$.getCurrentDelta();
            if (_lookRight) {
                _velocity.setX(jumpX);
            } else {
                _velocity.setX(-jumpX);
            }
            _jumping = true;
            _onGround = false;
        }
    }

    public void shoot() {
        if (_shotFired) {
            return;
        }
        _shootPower += (GameEngine.$.getCurrentDelta() * 0.0006f);
        if (_shootPower >= 1 || WeaponDirectShoot.class.isAssignableFrom(currentWeapon)) {
            EntityWeapon.fire(currentWeapon, _world, this, _position, _shootAngle, 1f);
            _shotFired = true;
            _shootPower = 0;
        }
    }

    public void resetPower() {
        if (_shootPower > 0) {
            EntityWeapon.fire(currentWeapon, _world, this, _position, _shootAngle, _shootPower);
        }
        _shootPower = 0;
        _shotFired = false;
    }

    public boolean canFall() {
        for (int tY = 1; tY < 2; tY++) {
            int bottomY = (int) (_position.getY() + _playerHeight + tY);
            for (int x = (int) -_playerWidth; x <= _playerWidth; x++) {
                if (_world.isPixelSolid((int) (_position.getX() + x), bottomY)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Vector2f getCollidingNormal() {
        int bottomY = (int) (_position.getY() + _playerHeight) + 1;
        for (int x = (int) -_playerWidth; x <= _playerWidth; x++) {
            if (_world.isPixelSolid((int) (_position.getX() + x), bottomY)) {
                return _world.getNormal((int) (_position.getX() + x), bottomY);
            }
        }
        return new Vector2f(0, 0);
    }

    @Override
    public void updatePhysics(int delta) {
        if(TerrainCore.CURRENT_PLAYER != this) {
            _movement[LEFT] = false;
            _movement[RIGHT] = false;
            _movement[UP] = false;
            _movement[DOWN] = false;
        }
        if (getPosition().getY() > _world.getHeight() - 20) {
            _health = 0;
        }
        delta = 16;
        updateAnimations(delta);

        // shoot angle
        float rotationSpeed = 0.05f;
        if ((_movement[UP] && _lookRight) || (_movement[DOWN] && !_lookRight)) {
            if ((_lookRight && _shootAngle > rotationSpeed) || (!_lookRight && _shootAngle > -170)) {
                _shootAngle -= rotationSpeed * delta;
            }
        } else if ((_movement[DOWN] && _lookRight) || (_movement[UP] && !_lookRight)) {
            if ((!_lookRight && _shootAngle < -rotationSpeed) || (_lookRight && _shootAngle < 170)) {
                _shootAngle += rotationSpeed * delta;
            }
        }

        // look left/right
        if (_lookRight && _shootAngle < 0) {
            _shootAngle = -_shootAngle;
        } else if (!_lookRight && _shootAngle > 0) {
            _shootAngle = -_shootAngle;
        }

        // get velocity
        float vX = _velocity.getX();
        float vY = _velocity.getY();

        // gravity
        boolean canFall = canFall();
        if (canFall) {
            vY += (0.015F * delta);
            vY = getMaxAdvanceY(vY);
        } else {
            // is on ground.. if vY < 0, we are jumping or flying high
            if (Math.abs(vX) < 0.1f) {
                _pushedByWeapon = false;
            }
            if (vY > 0) {
                vY = -0.0005f;

                Vector2f normal = getCollidingNormal();
                if (normal.getY() > -0.15f) {
                    vX += (normal.getX() / 16f);
                }
            }
        }

        // friction
        if (!_pushedByWeapon) {
            if (!_onGround) {
                vX *= 0.97f;
            } else {
                vX *= 0.99f;
            }
        } else {
            vX *= 0.99f;
        }

        // WALKING LEFT OR RIGHT
        if (_onGround && !_pushedByWeapon && TerrainCore.CURRENT_PLAYER == this) {
            float walkSpeed = 0.03f;
            if (_movement[LEFT] && !_movement[RIGHT]) {
                _lookRight = false;
                vX = -walkSpeed * delta;
            }
            if (_movement[RIGHT] && !_movement[LEFT]) {
                _lookRight = true;
                vX = +walkSpeed * delta;
            }
        }

        float maxAdvanceX = getMaxAdvanceX(vX);
        _position.move(maxAdvanceX, vY);
        _velocity.set(vX, vY);

        if (vX != maxAdvanceX) {
            int maxStepSize = 4;

            Vector2f normal = getCollidingNormal();
            if (canGoThere(maxStepSize, vX) && Math.abs(normal.getX()) < 0.98f) {
                int upShift = getUpshift(maxStepSize, vX);
                if (upShift != 0) {
                    _position.move((vX - maxAdvanceX) / (upShift * 2f), getMaxAdvanceY(-upShift));
                }
            }
        }

        _onGround = !canFall();
        if (_onGround) {
            if (!_animation.equals(ANIMATION_WALK)) {
                _animation = ANIMATION_WALK.clone();
            }
            if ((!_movement[LEFT] && !_movement[RIGHT] && !_pushedByWeapon) || (!_pushedByWeapon)) {
                vX = 0f;
            } else {
                vX *= 0.98f;
            }

            _jumping = false;
            _velocity.set(vX, vY);
        } else {
            vX *= 0.99f;
            _velocity.set(vX, vY);
        }
    }

    private void updateAnimations(final int delta) {
        if (!_jumping && (_movement[RIGHT] || _movement[LEFT]) && !_pushedByWeapon) {
            if (!_animation.equals(ANIMATION_WALK)) {
                _animation = ANIMATION_WALK.clone();
            }
            _animation.step(delta);
        } else {
            if (_pushedByWeapon || _jumping) {
                if (!_animation.equals(ANIMATION_JUMP)) {
                    _animation = ANIMATION_JUMP.clone();
                }
                if (_animation.getCurrentStep() < 7) {
                    _animation.step(delta);
                } else {
                    _animation.goToLastFrame();
                }
            } else {
                if (_onGround && !_animation.equals(ANIMATION_WALK)) {
                    _animation = ANIMATION_WALK.clone();
                }
                _animation.setCurrentFrame(3);
            }
        }
    }

    public void setPushedByWeapon(boolean pushedByWeapon) {
        _pushedByWeapon = pushedByWeapon;
        _onGround = false;
    }

    private boolean canGoThere(int steps, float vX) {
        if (canFall()) {
            return true;
        }

        int minX = (int) (_position.getX() + vX - _playerWidth);
        int maxX = (int) (_position.getX() + vX + _playerWidth);

        int bottomY = (int) (_position.getY() + _playerHeight - steps);
        for (int x = minX; x <= maxX; x++) {
            for (int currentStep = 1; currentStep <= _playerHeight * 2; currentStep++) {
                if (_world.isPixelSolid(x, bottomY - currentStep)) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getUpshift(int steps, float vX) {
        if (canFall()) {
            return 0;
        }

        int minX = (int) (_position.getX() + vX - _playerWidth);
        int maxX = (int) (_position.getX() + vX + _playerWidth + 1);

        int bottomY = (int) (_position.getY() + _playerHeight);
        int upShift = 0;
        for (int currentStep = 1; currentStep <= steps; currentStep++) {
            boolean rowFree = true;
            for (int x = minX; x <= maxX; x++) {
                if (_world.isPixelSolid(x, bottomY - currentStep)) {
                    rowFree = false;
                }
            }
            upShift++;
            if (rowFree) {
                return upShift;
            }
        }

        return steps;
    }

    private float getMaxAdvanceX(float vX) {
        if (vX > 0) {
            int rightX = (int) (_position.getX() + _playerWidth);
            float advanceX = 0f;
            for (int x = rightX; advanceX <= vX; advanceX++) {
                for (int y = (int) -_playerHeight; y <= _playerHeight; y++) {
                    if (_world.isPixelSolid((int) (x + advanceX), (int) (_position.getY() + y))) {
                        return Math.max(0, advanceX - 1f);
                    }
                }
            }
        } else if (vX < 0) {
            int leftX = (int) (_position.getX() - _playerWidth);
            float advanceX = 0f;
            for (int x = leftX; advanceX >= vX; advanceX--) {
                for (int y = (int) -_playerHeight; y <= _playerHeight; y++) {
                    if (_world.isPixelSolid((int) (x + advanceX), (int) (_position.getY() + y))) {
                        return Math.min(advanceX + 1f, 0f);
                    }
                }
            }
        }
        return vX;
    }

    private float getMaxAdvanceY(float vY) {
        if (vY > 0) {
            float bottomY = _position.getY() + _playerHeight + 1f;
            float advanceY = vY;
            float canAdvance = vY;
            for (float y = bottomY + advanceY; advanceY >= (-vY); advanceY--) {
                for (int x = (int) -_playerWidth; x <= _playerWidth; x++) {
                    if (_world.isPixelSolid((int) (_position.getX() + x), (int) y)) {
                        canAdvance = advanceY;
                    }
                }
            }
            if (canAdvance < 0.1f) {
                return 0;
            }
            return canAdvance;
        } else if (vY < 0) {
            float topY = _position.getY() - _playerHeight - 1f;
            float advanceY = vY;
            float canAdvance = vY;
            for (float y = topY - advanceY; advanceY <= (-vY); advanceY++) {
                for (int x = (int) -_playerWidth; x <= _playerWidth; x++) {
                    if (_world.isPixelSolid((int) (_position.getX() + x), (int) y)) {
                        canAdvance = advanceY;
                    }
                }
            }
            if (canAdvance > 0.1f) {
                return 0;
            }
            return canAdvance;
        }
        return vY;
    }

    @Override
    public void render() {
        if (getHealth() < 1) {
            return;
        }
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);

        glPushMatrix();
        {
            glTranslatef(_position.getX(), _position.getY() - 19, 0);
            if (_animation.equals(ANIMATION_JUMP)) {
                glTranslatef(0, 19, 0);
            }
            if (!_lookRight) {
                glScalef(-1, 1, 1);
            }

            _animation.render();
        }

        glPopMatrix();
        glTranslatef(_position.getX(), _position.getY(), 0);

//        renderDebugHitbox();

        // healthbar
        renderHealthBar();

        // crosshair
        if (TerrainCore.CURRENT_PLAYER == this) {
            if (!WeaponNoCrosshair.class.isAssignableFrom(currentWeapon)) {
                float crosshairDistance = 100f;
                float x2 = CROSSHAIR.getHalfWidth() - CROSSHAIR.getHalfWidth() / 2f;

                // powersign
                glPushMatrix();
                {
                    glDisable(GL_TEXTURE_2D);
                    glRotatef(_shootAngle, 0, 0, 1);
                    glBegin(GL_POLYGON);
                    {
                        glColor4f(0, 1, 0, 0.6f);
                        glVertex2f(0, 0);
                        glColor4f(1 * _shootPower, 1 * (1 - _shootPower), 0, 0.6f);
                        glVertex2f(-x2 * _shootPower, -crosshairDistance * _shootPower);
                        glVertex2f(-x2 / 1.75f * _shootPower, (-crosshairDistance - x2 / 4f - x2 / 8f) * _shootPower);
                        glVertex2f(0, (-crosshairDistance - x2 / 2f) * _shootPower);
                        glVertex2f(+x2 / 1.75f * _shootPower, (-crosshairDistance - x2 / 4f - x2 / 8f) * _shootPower);
                        glVertex2f(+x2 * _shootPower, -crosshairDistance * _shootPower);
                    }
                    glEnd();
                }
                glPopMatrix();

                // crosshair
                glPushMatrix();
                {
                    glRotatef(_shootAngle, 0, 0, 1);
                    glTranslatef(0, -crosshairDistance, 0);
                    glEnable(GL_TEXTURE_2D);
                    glEnable(GL_BLEND);
                    CROSSHAIR.render(1, 1, 1, 1);
                }
                glPopMatrix();
            }
        }
    }

    private void renderDebugHitbox() {
        glColor4f(1, 0, 0, 1);
        glBegin(GL_LINE_LOOP);
        {
            glVertex2f(-_playerWidth, -_playerHeight);
            glVertex2f(+_playerWidth, -_playerHeight);
            glVertex2f(+_playerWidth, +_playerHeight);
            glVertex2f(-_playerWidth, +_playerHeight);
        }
        glEnd();

        // view
        glPushMatrix();
        {
            glColor4f(0, 1, 0, 1);
            glBegin(GL_LINES);
            {
                glVertex2f(0, 0);
                if (_lookRight) {
                    glVertex2f(_playerWidth, 0);
                } else {
                    glVertex2f(-_playerWidth, 0);
                }
            }
            glEnd();
        }
        glPopMatrix();

        // shot angle
        glPushMatrix();
        {
            glColor4f(1, 1, 1, 1);
            glRotatef(_shootAngle - 90, 0, 0, 1);
            glBegin(GL_LINES);
            {
                glVertex2f(0, 0);
                glVertex2f(_playerWidth * 30, 0);
            }
            glEnd();
        }
        glPopMatrix();
    }

    private void renderHealthBar() {
        glPushMatrix();
        {
            // outline
            final int halfWidth = 22;
            final int halfHeight = 7;
            glPushMatrix();
            {
                // translate
                glTranslatef(0, -22, 0);
                if (_teamId == 0) {
                    glColor4f(1, 0, 0, 1);
                } else {
                    glColor4f(0, 1, 0, 1);
                }
                glRectf(-halfWidth, -halfHeight, +halfWidth, +halfHeight);
                // inner black border
                glColor4f(0, 0, 0, 1);
                glRectf(-halfWidth + 1, -halfHeight + 1, +halfWidth - 1, +halfHeight - 1);
            }
            glPopMatrix();

            // health
            glPushMatrix();
            {
                // translate
                glTranslatef(-20f, -23f, 0);
                glBegin(GL_QUADS);
                {
                    glColor4f(1, 0, 0, 1);
                    glVertex2f(0, -3.75f);
                    glVertex2f(0, +6);

                    int maxHealth = Math.min(100, getHealth());
                    maxHealth = Math.max(0, maxHealth);

                    float percent = (float) maxHealth / 100f;
                    glColor4f(1 - percent, percent, 0, 1);
                    glVertex2f(percent * 40.5f, +6);
                    glVertex2f(percent * 40.5f, -3.75f);
                }
                glEnd();
            }
            glPopMatrix();

            // text
            glPushMatrix();
            {
                // translate
                glTranslatef(0, -19, 0);
                glDisable(GL_DEPTH_TEST);
                glEnable(GL_BLEND);
                glEnable(GL_TEXTURE_2D);
                Color.white.bind();
                TextureImpl.bindNone();
                Font font = FontManager.getStandardFont();
                font.drawString(-(font.getWidth("" + _health) / 2), -10, "" + _health);
            }
            glPopMatrix();
        }
        glPopMatrix();
    }

    public void setMovement(boolean... args) {
        for (int i = 0; i < args.length; i++) {
            _movement[i] = args[i];
        }
    }

    public int addHealth(final int health) {
        _health += health;
        _health = Math.max(0, _health);
        _health = Math.min(250, _health);
        return _health;
    }

    public void setHealth(int health) {
        _health = health;
    }

    public int getHealth() {
        return _health;
    }

    public void setWeapon(Class<? extends EntityWeapon> clazz) {
        if (clazz == null) {
            return;
        }

        currentWeapon = clazz;
    }

    public String getCurrentWeaponName() {
        return currentWeapon.getSimpleName().replaceAll("Entity", "");
    }

    public Class<? extends EntityWeapon> getCurrentWeaponClass() {
        return currentWeapon;
    }

    // ///////////////////////////////////////////////////////////////
    //
    // PhysicsObject
    //
    // ///////////////////////////////////////////////////////////////

    @Override
    public Vector2f getPosition() {
        return _position;
    }

    @Override
    public Vector2f getVelocity() {
        return _velocity;
    }

    @Override
    public void setPosition(Vector2f position) {
        _position.set(position.getX(), position.getY());
    }

    @Override
    public void setVelocity(Vector2f velocity) {
        _velocity.set(velocity.getX(), velocity.getY());
    }

    public void addVelocity(Vector2f toVector) {
        _velocity.set(_velocity.getX() + toVector.getX(), _velocity.getY() + toVector.getY());
    }
}
