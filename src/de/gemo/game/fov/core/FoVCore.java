package de.gemo.game.fov.core;

import java.util.*;

import de.gemo.game.fov.navigation.*;
import de.gemo.game.fov.units.*;
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
        int lightCount = 5;
        int blockCount = 150;

        for (int i = 1; i <= lightCount; i++) {
            Vector3f location = new Vector3f((float) Math.random() * this.VIEW_WIDTH, (float) Math.random() * this.VIEW_HEIGHT, 0);
            enemies.add(new Enemy(location));
        }

        for (int i = 1; i <= blockCount; i++) {
            int width = 15;
            int height = 15;
            int x = (int) (Math.random() * (this.VIEW_WIDTH - width));
            int y = (int) (Math.random() * (this.VIEW_HEIGHT - height));
            tiles.add(new Tile(x, y, width, height));
        }

        coneShader = new Shader();
        coneShader.loadPixelShader("shaders/viewcone.frag");

        ambientShader = new Shader();
        // ambientShader.loadPixelShader("ambientLight.frag");

        this.navMesh = new NavMesh(this.tiles);
    }

    @Override
    public void onMouseDown(boolean handled, MouseClickEvent event) {
        if (event.isRightButton()) {
            for (Enemy enemy : this.enemies) {
                enemy.setTarget(new Vector3f(event.getX(), event.getY(), 0), navMesh, this.tiles);
            }
        }
    }

    private void updateLights() {
        // lights.get(0).setAngle(getAngle(lights.get(0).getLocation(),
        // MouseManager.INSTANCE.getMouseVector()));
        int i = 0;
        for (Enemy enemy : this.enemies) {
            if (i == 0) {
                enemy.setAlerted(true);
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
        glUniform4f(glGetUniformLocation(ambientShader.getID(), "ambientColor"), 0.3f, 0.3f, 0.7f, 0.5f);

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
        if (this.navMesh.path != null) {
            this.navMesh.path.render();
        } else {
        }

    }

    @Override
    protected void onShutdown(boolean error) {
        this.coneShader.cleanup();
        this.ambientShader.cleanup();
    }

    public float getAngle(Vector3f target, Vector3f pos) {
        return pos.getAngle(target);
    }
}
