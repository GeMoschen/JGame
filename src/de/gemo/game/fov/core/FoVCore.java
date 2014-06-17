package de.gemo.game.fov.core;

import java.util.*;

import de.gemo.game.fov.navigation.*;
import de.gemo.game.fov.units.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.events.mouse.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class FoVCore extends GameEngine {

    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private NavMesh navMesh;

    private Shader coneShader, ambientShader;

    public FoVCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        int lightCount = 1;
        int blockCount = 10;

        for (int i = 1; i <= blockCount; i++) {
            int width = 15;
            int height = 15;
            int x = (int) (20 + (Math.random() * (this.VIEW_WIDTH - width - 20)));
            int y = (int) (20 + (Math.random() * (this.VIEW_HEIGHT - height - 20)));
            tiles.add(new Tile(x, y, width, height));
        }

        for (int i = 1; i <= lightCount; i++) {
            boolean freeSpace = false;
            Vector3f location = null;
            while (!freeSpace) {
                freeSpace = true;
                location = new Vector3f(20 + (float) Math.random() * (this.VIEW_WIDTH - 20), 20 + (float) Math.random() * (this.VIEW_HEIGHT - 20), 0);
                for (Tile tile : this.tiles) {
                    if (CollisionHelper.isVectorInHitbox(location, tile.expanded)) {
                        freeSpace = false;
                        break;
                    }
                }
            }
            enemies.add(new Enemy(location));
        }

        coneShader = new Shader();
        coneShader.loadPixelShader("shaders/viewcone.frag");

        ambientShader = new Shader();
        ambientShader.loadPixelShader("shaders/ambientLight.frag");

        this.navMesh = new NavMesh(this.tiles);
    }

    @Override
    public void onMouseDown(boolean handled, MouseClickEvent event) {
        if (event.isLeftButton()) {
            for (Enemy enemy : this.enemies) {

                enemy.update(this.navMesh, this.tiles);
                // enemy.setTarget(new Vector3f(event.getX(), event.getY(), 0),
                // navMesh, this.tiles);
            }
        }

        if (event.isMiddleButton()) {
            for (Enemy enemy : this.enemies) {

                // enemy.update(this.navMesh, this.tiles);
                enemy.setTarget(new Vector3f(event.getX(), event.getY(), 0), navMesh, this.tiles);
            }
        }
    }

    @Override
    public void onMouseWheel(boolean handled, MouseWheelEvent event) {
        for (Enemy enemy : this.enemies) {
            enemy.update(this.navMesh, this.tiles);
            // enemy.setTarget(new Vector3f(event.getX(), event.getY(), 0),
            // navMesh, this.tiles);
        }
    }

    private void updateLights() {
        // lights.get(0).setAngle(getAngle(lights.get(0).getLocation(),
        // MouseManager.INSTANCE.getMouseVector()));
        int i = 0;
        for (Enemy enemy : this.enemies) {

            if (i == 0) {
                // enemy.setAlerted(true);
            }
            i++;
            enemy.update(this.navMesh, this.tiles);

            // if (!cone.collides(lights.get(0))) {
            // cone.update();
            // // cone.setAlerted(false);
            // } else {
            // // create raycast
            // Hitbox raycast = new Hitbox(0, 0);
            // raycast.addPoint(cone.getLocation());
            // raycast.addPoint(lights.get(0).getLocation());
            //
            // // check for colliding polys
            // boolean canSeeTarget = false;
            // for (Tile block : this.blocks) {
            // if (CollisionHelper.findIntersection(raycast, block.getHitbox())
            // != null) {
            // canSeeTarget = true;
            // break;
            // }
            // }
            // if (canSeeTarget) {
            // cone.update();
            // cone.setAlerted(false);
            // } else {
            // cone.setAlerted(true);
            // cone.setAngle(getAngle(cone.getLocation(),
            // lights.get(0).getLocation()));
            // }
            // }
        }
    }

    @Override
    protected void updateGame(int delta) {
        this.updateLights();
    }

    @Override
    protected void renderGame2D() {

        // blocks
        for (Tile block : tiles) {
            glColor4f(1f, 1f, 1f, 0.5f);
            block.renderHitbox();
        }

        // AMBIENT
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        ambientShader.bind();
        glUniform4f(glGetUniformLocation(ambientShader.getID(), "ambientColor"), 0.3f, 0.3f, 0.7f, 1f);

        glColor4f(0.3f, 0.3f, 0.7f, 0.5f);
        glBegin(GL_QUADS);
        {
            glVertex2f(0, 0);
            glVertex2f(this.VIEW_WIDTH, 0);
            glVertex2f(this.VIEW_WIDTH, this.VIEW_HEIGHT);
            glVertex2f(0, this.VIEW_HEIGHT);
        }
        glEnd();
        ambientShader.unbind();
        glDisable(GL_BLEND);

        // render lightcones
        for (Enemy light : enemies) {
            light.render(this.tiles, this.coneShader, this.ambientShader, this.VIEW_WIDTH, this.VIEW_HEIGHT);
        }

        // this.navMesh.createNavMesh(this.tiles);
        this.navMesh.render();
    }

    @Override
    protected void onShutdown(boolean error) {
        this.coneShader.cleanup();
        this.ambientShader.cleanup();
    }
}
