package de.gemo.game.polygon.core;

import de.gemo.game.polygon.entities.Enemy;
import de.gemo.game.polygon.navigation.NavMesh;
import de.gemo.game.polygon.navigation.Path;
import de.gemo.gameengine.collision.CollisionHelper;
import de.gemo.gameengine.collision.Hitbox;
import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.events.mouse.MouseMoveEvent;
import de.gemo.gameengine.events.mouse.MouseReleaseEvent;
import de.gemo.gameengine.events.mouse.MouseWheelEvent;
import de.gemo.gameengine.units.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;
import static org.lwjgl.opengl.GL11.*;

public class PolygonCore extends GameEngine {

    private final List<Hitbox> _viewObstacles = new ArrayList<>();
    private final List<Hitbox> _walkObstacles = new ArrayList<>();
    private NavMesh _navMesh;
    private Path _path;

    private Enemy _enemy;

    public PolygonCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, false);
    }

    @Override
    protected void createManager() {
        final Random random = new Random();
        for (int i = 0; i < 100; i++) {
            final Hitbox obstacle = new Hitbox(random.nextInt(1024), random.nextInt(768));
            obstacle.addPoint(-10, -10);
            obstacle.addPoint(0, -15);
            obstacle.addPoint(+10, -10);
            obstacle.addPoint(+10, 10);
            obstacle.addPoint(0, 15);
            obstacle.addPoint(-10, 10);
            obstacle.rotate(random.nextFloat() * 360);
            _viewObstacles.add(obstacle);
        }

        createWalkObstacles();

        _navMesh = new NavMesh(_walkObstacles);

        Vector3f pos = new Vector3f(random.nextInt(1024), random.nextInt(768), 0);
        boolean valid = false;

        while (!valid) {
            valid = true;
            for (final Hitbox obstacle : _viewObstacles) {
                if (CollisionHelper.isVectorInHitbox(pos, obstacle)) {
                    valid = false;
                    pos = new Vector3f(random.nextInt(1024), random.nextInt(768), 0);
                    break;
                }
            }
        }

        _enemy = new Enemy(pos.getX(), pos.getY(), 0);
    }

    private void createWalkObstacles() {
        _walkObstacles.clear();

        _walkObstacles.add(createHitbox(0, 300, 400, 50, 0));
        _walkObstacles.add(createHitbox(1024 - 400, 500, 400, 50, 0));
        _walkObstacles.add(createHitbox(500, 0, 60, 200, 0));
        _walkObstacles.add(createHitbox(400, 768 - 200, 60, 200, 0));

        _walkObstacles.addAll(_viewObstacles);
    }

    private Hitbox createHitbox(final float x, final float y, final float width, final float height, final float angle) {
        float a = new Random().nextFloat() * 90;
        float hW = width / 2f;
        float hH = height / 2f;
        final Hitbox hitbox = new Hitbox(x + hW, y + hH);
        hitbox.addPoint(-hW, -hH);
        hitbox.addPoint(+hW, -hH);
        hitbox.addPoint(+hW, +hH);
        hitbox.addPoint(-hW, +hH);
        hitbox.rotate(a);
        return hitbox;
    }

    @Override
    protected void renderGame2D() {
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);
        glClearColor(0.1f, 0.1f, 0.1f, 1f);
        glDisable(GL_TEXTURE_2D);

        glDisable(GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_TEXTURE_2D);

        glPushMatrix();
        {
            for (final Hitbox obstacle : _viewObstacles) {
                obstacle.render();
            }

            for (final Hitbox obstacle : _walkObstacles) {
                obstacle.render();
            }

            _navMesh.render();
            if (_path != null) {
                _path.render();
            }
            _enemy.render();
        }
        glPopMatrix();
    }


    @Override
    public void onMouseUp(boolean handled, MouseReleaseEvent event) {
        if (event.isRightButton()) {
            _enemy = new Enemy(event.getX(), event.getY(), 0);
        } else if (event.isLeftButton()) {
            final Random random = new Random();
            final Hitbox obstacle = new Hitbox(event.getX(), event.getY());
            obstacle.addPoint(-10, -10);
            obstacle.addPoint(0, -15);
            obstacle.addPoint(+10, -10);
            obstacle.addPoint(+10, 10);
            obstacle.addPoint(0, 15);
            obstacle.addPoint(-10, 10);
            obstacle.rotate(random.nextFloat() * 360);
            _viewObstacles.add(obstacle);
            createWalkObstacles();
            _navMesh = new NavMesh(_walkObstacles);
        } else if (event.isMiddleButton()) {
            _path = _navMesh.findPath(_enemy.getPosition().clone(), getMouseManager().getMouseVector().clone());
            _enemy.setPath(_path);
        }
    }

    @Override
    public void onMouseMove(boolean handled, MouseMoveEvent event) {
    }

    @Override
    public void onMouseWheel(boolean handled, MouseWheelEvent event) {
    }

    @Override
    protected void updateGame(int delta) {
    }

    @Override
    protected void tickGame(int delta) {
        _enemy.updatePosition();
        _enemy.updateCollisions(_viewObstacles);
    }
}
