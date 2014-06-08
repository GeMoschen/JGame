package de.gemo.game.fov.core;

import java.util.*;

import org.lwjgl.input.*;
import de.gemo.game.fov.units.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.manager.*;
import de.gemo.gameengine.units.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class FoVCore extends GameEngine {

    private ArrayList<LightCone> lights = new ArrayList<LightCone>();
    private ArrayList<Tile> blocks = new ArrayList<Tile>();

    private Shader coneShader, ambientShader;

    public FoVCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        int lightCount = 20;
        int blockCount = 25;
        // int blockCount = 3;

        for (int i = 1; i <= lightCount; i++) {
            Vector3f location = new Vector3f((float) Math.random() * this.VIEW_WIDTH, (float) Math.random() * this.VIEW_HEIGHT, 0);
            lights.add(new LightCone(location, (float) Math.random() * 10, (float) Math.random() * 10, (float) Math.random() * 10));
        }

        for (int i = 1; i <= blockCount; i++) {
            int width = 30;
            int height = 30;
            int x = (int) (Math.random() * (this.VIEW_WIDTH - width));
            int y = (int) (Math.random() * (this.VIEW_HEIGHT - height));
            blocks.add(new Tile(x, y, width, height));
        }

        coneShader = new Shader();
        coneShader.loadPixelShader("shaders/viewcone.frag");

        ambientShader = new Shader();
        // ambientShader.loadPixelShader("ambientLight.frag");
    }

    private void updateLights() {
        lights.get(0).intensity = 1f;
        lights.get(0).red = 30f;
        lights.get(0).green = 0f;
        lights.get(0).blue = 0f;
        if (Mouse.isButtonDown(0)) {
            lights.get(0).target = MouseManager.INSTANCE.getMouseVector().clone();
        }

        lights.get(0).setAngle(getAngle(lights.get(0).getLocation(), MouseManager.INSTANCE.getMouseVector()));
        lights.get(0).seek(this.lights);
        int i = 0;
        for (LightCone cone : this.lights) {
            if (i != 0) {
                if (!cone.collides(lights.get(0))) {
                    cone.update();
                    cone.setAlerted(false);
                } else {
                    // create raycast
                    Hitbox raycast = new Hitbox(0, 0);
                    raycast.addPoint(cone.getLocation());
                    raycast.addPoint(lights.get(0).getLocation());

                    // check for colliding polys
                    boolean canSeeTarget = false;
                    for (Tile block : this.blocks) {
                        if (CollisionHelper.findIntersection(raycast, block.getHitbox()) != null) {
                            canSeeTarget = true;
                            break;
                        }
                    }
                    if (canSeeTarget) {
                        cone.update();
                        cone.setAlerted(false);
                    } else {
                        cone.setAlerted(true);
                        cone.setAngle(getAngle(cone.getLocation(), lights.get(0).getLocation()));
                    }
                }
            }
            i = 1;
        }
    }

    @Override
    protected void updateGame(int delta) {
        this.updateLights();
    }

    private void angleTest(Tile block) {
        Hitbox hitbox = block.getHitbox().clone();
        hitbox.scaleByPixel(-10);
        hitbox.render();
    }

    @Override
    protected void renderGame2D() {

        // blocks
        for (Tile block : blocks) {
            glColor3f(1f, 1f, 1f);
            block.renderHitbox();
            this.angleTest(block);
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
        for (LightCone light : lights) {
            light.render(this.blocks, this.coneShader, this.ambientShader, this.VIEW_WIDTH, this.VIEW_HEIGHT);
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
