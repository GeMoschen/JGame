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

    private Vector2f v1, v2, v3;

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
            int width = 50;
            int height = 50;
            int x = (int) (Math.random() * (this.VIEW_WIDTH - width));
            int y = (int) (Math.random() * (this.VIEW_HEIGHT - height));
            blocks.add(new Block(x, y, width, height));
        }

        coneShader = new Shader();
        coneShader.loadPixelShader("viewcone.frag");

        ambientShader = new Shader();
        // ambientShader.loadPixelShader("ambientLight.frag");

        v3 = new Vector2f(100, 100);
        v2 = new Vector2f(200, 100);
        v1 = new Vector2f(300, 100);
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

        v2.x = MouseManager.INSTANCE.getMouseVector().getX();
        v2.y = MouseManager.INSTANCE.getMouseVector().getY();
    }

    @Override
    protected void updateGame(int delta) {
        this.updateLights();
    }

    private void angleTest() {

        glDisable(GL_BLEND);
        glColor4f(1, 0, 0, 1);
        glBegin(GL_LINES);
        glVertex2f(v1.x, v1.y);
        glVertex2f(v2.x, v2.y);

        glVertex2f(v2.x, v2.y);
        glVertex2f(v3.x, v3.y);
        glEnd();

        float a1 = getAngle(v2, v3);
        float a2 = getAngle(v1, v2);
        float allA = 180 + a1 + a2;
        allA /= 2;

        if (a1 > 90) {
            allA -= 180;
        }

        de.gemo.gameengine.units.Vector2f v4 = new de.gemo.gameengine.units.Vector2f(v2.x, v2.y - 20);

        float rad = (float) Math.toRadians(allA);
        float sin = (float) Math.sin(rad);
        float cos = (float) Math.cos(rad);
        v4.rotateAround(new de.gemo.gameengine.units.Vector2f(v2.x, v2.y), sin, cos);

        glColor4f(1, 0, 1, 1);
        glBegin(GL_LINES);
        glVertex2f(v2.x, v2.y);
        glVertex2f(v4.getX(), v4.getY());
        glEnd();
    }

    @Override
    protected void renderGame2D() {
        this.angleTest();

        // blocks
        glColor3f(1f, 1f, 1f);
        for (Block block : blocks) {
            block.render();
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
}
