package de.gemo.game.test;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {

    private final int WIN_WIDTH = 1024;
    private final int WIN_HEIGHT = 768;

    private final int VIEW_WIDTH = 1024;
    private final int VIEW_HEIGHT = 768;

    /**
     * @param args
     */
    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        try {
            Display.setDisplayMode(new DisplayMode(WIN_WIDTH, WIN_HEIGHT));
            Display.setTitle("JGame");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        // init OpenGL
        glMatrixMode(GL_PROJECTION);
        glOrtho(0, VIEW_WIDTH, VIEW_HEIGHT, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        while (!Display.isCloseRequested()) {
            // clear contents
            glClear(GL_COLOR_BUFFER_BIT);

            // render

            glBegin(GL_LINE_LOOP);

            glColor3f(0, 1.0f, 0);
            glVertex2i(100, 100);
            glVertex2i(100, 200);            
            glVertex2i(200, 200);
            glEnd();

            // update and sync
            Display.update();
            Display.sync(60);
        }

        Display.destroy();
        System.exit(0);
    }
}
