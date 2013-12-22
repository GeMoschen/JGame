package de.gemo.game.physics;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2i;

import org.lwjgl.input.Keyboard;

import de.gemo.gameengine.core.GameEngine;
import de.gemo.gameengine.events.keyboard.KeyEvent;

public class GLSL extends GameEngine {

    public GLSL(String windowTitle, int windowWidth, int windowHeight, boolean fullscreen) {
        super(windowTitle, windowWidth, windowHeight, fullscreen);
    }

    private Shader shader;

    @Override
    protected void createManager() {
        this.shader = new Shader();
        shader.loadPixelShader("resources\\shader\\pixelshader.frag");
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
        if (event.getKey() == Keyboard.KEY_F1) {
            shader.loadPixelShader("resources\\shader\\pixelshader.frag");
        }
    }

    @Override
    protected void renderGame2D() {
        shader.bind();
        glBegin(GL_QUADS);
        {
            glVertex2i(100, 100);
            glVertex2i(300, 100);
            glVertex2i(300, 300);
            glVertex2i(100, 300);
        }
        glEnd();
        shader.unbind();
    }
}
