package de.gemo.game.skyroads;

import jinngine.geometry.Box;
import jinngine.physics.Body;
import jinngine.physics.Scene;

import org.newdawn.slick.Color;

import de.gemo.game.engine.model.Model;

import static org.lwjgl.opengl.GL11.*;

public class Cube {
    private Color color, lineColor;
    private Model model, lineModel;

    public static Body createBox(float halfWidth, float halfHeight, float halfDepth) {
        // add boxes to bound the world
        Body body = new Body("floor", new Box(halfWidth * 2f, halfHeight * 2f, halfDepth * 2f));
        return body;
    }

    public static Body createBox(float x, float y, float z, float halfWidth, float halfHeight, float halfDepth) {
        // add boxes to bound the world
        Body body = createBox(halfWidth, halfHeight, halfDepth);
        body.setPosition(x, y, z);
        return body;
    }

    public static Body createBox(float x, float y, float z, float halfWidth, float halfHeight, float halfDepth, boolean fixed) {
        // add boxes to bound the world
        Body body = createBox(x, y, z, halfWidth, halfHeight, halfDepth);
        body.setFixed(fixed);
        return body;
    }

    public Cube(Model model, Model lineModel, Scene scene, int x, int y) {
        this.model = model;
        this.lineModel = lineModel;
        this.color = Color.white;
        this.lineColor = Color.yellow;
        this.createBody(scene, x, y);
    }

    private void createBody(Scene scene, int x, int y) {
        // add boxes to bound the world
        Body floor = createBox(x * 4, -2, -y * 10, 2, 2.5f, 5, true);
        scene.addBody(floor);
    }

    public void render() {
        this.render(true);
    }

    public void render(boolean drawWireframe) {
        glPushMatrix();

        this.color = new Color(200, 200, 200);
        glColor4f(this.color.r, this.color.g, this.color.b, 1f);

        this.model.render();
        glPopMatrix();

        if (drawWireframe) {
            glPushMatrix();
            // this.lineColor.bind();

            this.lineColor = new Color(0, 165, 255);
            glColor4f(this.lineColor.r, this.lineColor.g, this.lineColor.b, 1f);
            this.lineModel.render();
            glPopMatrix();
        }
    }

}
