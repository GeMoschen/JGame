package de.gemo.game.fov.core;

import java.util.*;

import org.lwjgl.input.*;
import org.lwjgl.util.vector.*;

import de.gemo.game.fov.units.*;
import de.gemo.gameengine.collision.*;
import de.gemo.gameengine.core.*;
import de.gemo.gameengine.manager.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class FoVCore extends GameEngine {

    private ArrayList<LightCone> lights = new ArrayList<LightCone>();
    private ArrayList<Block> blocks = new ArrayList<Block>();

    private Shader coneShader, ambientShader;

    public FoVCore(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    @Override
    protected void createManager() {
        int lightCount = 10;
        int blockCount = 25;
        // int blockCount = 3;

        for (int i = 1; i <= lightCount; i++) {
            Vector2f location = new Vector2f((float) Math.random() * this.VIEW_WIDTH, (float) Math.random() * this.VIEW_HEIGHT);
            lights.add(new LightCone(location, (float) Math.random() * 10, (float) Math.random() * 10, (float) Math.random() * 10));
        }

        for (int i = 1; i <= blockCount; i++) {
            int width = 30;
            int height = 30;
            int x = (int) (Math.random() * (this.VIEW_WIDTH - width));
            int y = (int) (Math.random() * (this.VIEW_HEIGHT - height));
            blocks.add(new Block(x, y, width, height));
        }

        coneShader = new Shader();
        coneShader.loadPixelShader("viewcone.frag");

        ambientShader = new Shader();
        // ambientShader.loadPixelShader("ambientLight.frag");
    }

    private void updateLights() {
        lights.get(0).intensity = 1f;
        lights.get(0).red = 30f;
        lights.get(0).green = 0f;
        lights.get(0).blue = 0f;
        if (Mouse.isButtonDown(0)) {
            lights.get(0).target = new Vector2f(MouseManager.INSTANCE.getMouseVector().getX(), MouseManager.INSTANCE.getMouseVector().getY());
        }

        lights.get(0).setAngle(getAngle(lights.get(0).getLocation(), new Vector2f(MouseManager.INSTANCE.getMouseVector().getX(), MouseManager.INSTANCE.getMouseVector().getY())));
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
                    raycast.addPoint(cone.getLocation().x, cone.getLocation().y);
                    raycast.addPoint(lights.get(0).getLocation().x, lights.get(0).getLocation().y);

                    // check for colliding polys
                    boolean canSeeTarget = false;
                    for (Block block : this.blocks) {
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

    private void angleTest(Block block) {
        Hitbox hitbox = block.getHitbox().clone();
        hitbox.scaleByPixel(15);
        hitbox.render();
    }

    @Override
    protected void renderGame2D() {

        // blocks
        for (Block block : blocks) {
            glColor3f(1f, 1f, 1f);
            block.render();
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

    public float getAngle(Vector2f target, Vector2f pos) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - pos.y, target.x - pos.x));

        if (angle < 0) {
            angle += 360;
        }

        return angle - 90;
    }

    public float getAngle(de.gemo.gameengine.units.Vector3f target, de.gemo.gameengine.units.Vector3f pos) {
        float angle = (float) Math.toDegrees(Math.atan2(target.getY() - pos.getY(), target.getX() - pos.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle - 90;
    }
}
