package de.gemo.game.core;

/*
 * Copyright (c) 2013, Oskar Veerhoek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.PNGDecoder.Format;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * A LWJGL port of the awesome MineFront Pre-ALPHA 0.02 Controls: W/UP = forward; A/LEFT = strafe left; D/RIGHT = strafe right; S/DOWN = backward; SPACE = fly up; SHIFT = fly down; CONTROL = move faster; TAB = move slower; Q = increase walking speed; Z = decrease walking speed; O = increase mouse speed; L = decrease mouse speed; C = reset position
 * 
 * @author Oskar Veerhoek, Yan Chernikov
 */
public class MineFront {

    /** Defines if the application is resizable. */
    private static final boolean resizable = true;
    /*
     * Defines if the application is running. Set to false to terminate the program.
     */
    private static volatile boolean running = true;
    /** The position of the player as a 3D vector (xyz). */
    private static Vector3f position = new Vector3f(0, 0, 0);
    /**
     * The rotation of the axis (where to the player looks). The X component stands for the rotation along the x-axis, where 0 is dead ahead, 180 is backwards, and 360 is automically set to 0 (dead ahead). The value must be between (including) 0 and 360. The Y component stands for the rotation along the y-axis, where 0 is looking straight ahead, -90 is straight up, and 90 is straight down. The value must be between (including) -90 and 90.
     */
    private static Vector3f rotation = new Vector3f(0, 0, 0);
    /** The minimal distance from the camera where objects are rendered. */
    private static final float zNear = 0.3f;
    /**
     * The width and length of the floor and ceiling. Don't put anything above 1000, or OpenGL will start to freak out, though.
     */
    private static final int gridSize = 10;
    /**
     * The size of tiles, where 0.5 is the standard size. Increasing the size by results in smaller tiles, and vice versa.
     */
    private static final float tileSize = 0.20f;
    /** The maximal distance from the camera where objects are rendered. */
    private static final float zFar = 20f;
    /** The distance where fog starts appearing. */
    private static final float fogNear = 9f;
    /** The distance where the fog stops appearing (fully black here) */
    private static final float fogFar = 13f;
    /** The color of the fog in rgba. */
    private static final Color fogColor = new Color(0f, 0f, 0f, 1f);
    /** Defines if the application utilizes full-screen. */
    private static final boolean fullscreen = false;
    /** Defines the walking speed, where 10 is the standard. */
    private static int walkingSpeed = 10;
    /** Defines the mouse speed. */
    private static int mouseSpeed = 2;
    /** Defines if the application utilizes vertical synchronization (eliminates screen tearing; caps fps to 60fps) */
    private static final boolean vsync = true;
    /** Defines if the applications prints its frames-per-second to the console. */
    private static final boolean printFPS = false;
    /** Defines the maximum angle at which the player can look up. */
    private static final int maxLookUp = 85;
    /** Defines the minimum angle at which the player can look down. */
    private static final int maxLookDown = -85;
    /** The height of the ceiling. */
    private static final float ceilingHeight = 10;
    /** The height of the floor. */
    private static final float floorHeight = -1;
    /** Defines the field of view. */
    private static final int fov = 68;
    private static int fps;
    private static long lastFPS;
    private static long lastFrame;

    private static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private static int getDelta() {
        long currentTime = getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = getTime();
        return delta;
    }

    private static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            if (printFPS) {
                System.out.println("FPS: " + fps);
            }
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    public static void main(String[] args) {
        try {
            if (fullscreen) {
                Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
            } else {
                Display.setResizable(resizable);
                Display.setDisplayMode(new DisplayMode(800, 600));
            }
            Display.setTitle("Minefront Pre-Alpha 0.02 LWJGL Port");
            Display.setVSyncEnabled(vsync);
            Display.create();
        } catch (LWJGLException ex) {
            ex.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        if (fullscreen) {
            Mouse.setGrabbed(true);
        } else {
            Mouse.setGrabbed(false);
        }

        if (!GLContext.getCapabilities().OpenGL11) {
            System.err.println("Your OpenGL version doesn't support the required functionality.");
            Display.destroy();
            System.exit(1);
        }

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_FOG);

        {
            FloatBuffer fogColours = BufferUtils.createFloatBuffer(4);
            fogColours.put(new float[]{fogColor.r, fogColor.g, fogColor.b, fogColor.a});
            glClearColor(fogColor.r, fogColor.g, fogColor.b, fogColor.a);
            fogColours.flip();
            glFog(GL_FOG_COLOR, fogColours);
            glFogi(GL_FOG_MODE, GL_LINEAR);
            glHint(GL_FOG_HINT, GL_NICEST);
            glFogf(GL_FOG_START, fogNear);
            glFogf(GL_FOG_END, fogFar);
            glFogf(GL_FOG_DENSITY, 0.005f);
        }

        int floorTexture = glGenTextures();
        {
            InputStream in = null;
            try {
                in = new FileInputStream("textures/ui/mainmenu.png");
                PNGDecoder decoder = new PNGDecoder(in);
                ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
                decoder.decode(buffer, decoder.getWidth() * 4, Format.RGBA);
                buffer.flip();
                glBindTexture(GL_TEXTURE_2D, floorTexture);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
                glBindTexture(GL_TEXTURE_2D, 0);
            } catch (FileNotFoundException ex) {
                System.err.println("Failed to find the texture files.");
                ex.printStackTrace();
                Display.destroy();
                System.exit(1);
            } catch (IOException ex) {
                System.err.println("Failed to load the texture files.");
                ex.printStackTrace();
                Display.destroy();
                System.exit(1);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        int ceilingDisplayList = glGenLists(1);
        glNewList(ceilingDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, ceilingHeight, gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, ceilingHeight, gridSize);
        glEnd();
        glEndList();

        int wallDisplayList = glGenLists(1);
        glNewList(wallDisplayList, GL_COMPILE);

        glBegin(GL_QUADS);

        // North wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);

        // West wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, floorHeight, +gridSize);

        // East wall

        glTexCoord2f(0, 0);
        glVertex3f(+gridSize, floorHeight, -gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(+gridSize, floorHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, -gridSize);

        // South wall

        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(-gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, ceilingHeight, +gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(+gridSize, floorHeight, +gridSize);

        glEnd();

        glEndList();

        int floorDisplayList = glGenLists(1);
        glNewList(floorDisplayList, GL_COMPILE);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex3f(-gridSize, floorHeight, -gridSize);
        glTexCoord2f(0, gridSize * 10 * tileSize);
        glVertex3f(-gridSize, floorHeight, gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, gridSize * 10 * tileSize);
        glVertex3f(gridSize, floorHeight, gridSize);
        glTexCoord2f(gridSize * 10 * tileSize, 0);
        glVertex3f(gridSize, floorHeight, -gridSize);
        glEnd();
        glEndList();

        int objectDisplayList = glGenLists(1);
        glNewList(objectDisplayList, GL_COMPILE);
        {
            double topPoint = 0.75;
            glBegin(GL_TRIANGLES);
            glColor4f(1, 1, 0, 1f);
            glVertex3d(0, topPoint, -5);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -0.75, -4);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -.75, -4);

            glColor4f(1, 1, 0, 1f);
            glVertex3d(0, topPoint, -5);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -0.75, -4);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -0.75, -6);

            glColor4f(1, 1, 0, 1f);
            glVertex3d(0, topPoint, -5);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -0.75, -6);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -.75, -6);

            glColor4f(1, 1, 0, 1f);
            glVertex3d(0, topPoint, -5);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -0.75, -6);
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -.75, -4);

            glEnd();
            glColor4f(1, 1, 1, 1);
        }
        glEndList();

        getDelta();
        lastFPS = getTime();

        while (running) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            int delta = getDelta();
            glBindTexture(GL_TEXTURE_2D, floorTexture);

            glEnable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glCallList(floorDisplayList);
            glCallList(ceilingDisplayList);
            glCallList(wallDisplayList);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);
            glBindTexture(GL_TEXTURE_2D, 0);
            glCallList(objectDisplayList);

            glLoadIdentity();
            glRotatef(rotation.x, 1, 0, 0);
            glRotatef(rotation.y, 0, 1, 0);
            glRotatef(rotation.z, 0, 0, 1);
            glTranslatef(position.x, position.y, position.z);

            if (Mouse.isGrabbed()) {
                float mouseDX = Mouse.getDX() * mouseSpeed * 0.16f;
                float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
                if (rotation.y + mouseDX >= 360) {
                    rotation.y = rotation.y + mouseDX - 360;
                } else if (rotation.y + mouseDX < 0) {
                    rotation.y = 360 - rotation.y + mouseDX;
                } else {
                    rotation.y += mouseDX;
                }
                if (rotation.x - mouseDY >= maxLookDown && rotation.x - mouseDY <= maxLookUp) {
                    rotation.x += -mouseDY;
                } else if (rotation.x - mouseDY < maxLookDown) {
                    rotation.x = maxLookDown;
                } else if (rotation.x - mouseDY > maxLookUp) {
                    rotation.x = maxLookUp;
                }
            }

            // If you're looking for a challenge / something interesting, be sure to have a look at this comment:
            // http://www.youtube.com/watch?v=OO_yNzAuDe4&lc=2e3e-Xz131-fklyBuY6e-xYiWWBv379j7BmQpZRysjc
            boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
            boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
            boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
            boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
            boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
            boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
            boolean moveFaster = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
            boolean moveSlower = Keyboard.isKeyDown(Keyboard.KEY_TAB);

            if (moveFaster && !moveSlower) {
                walkingSpeed *= 4f;
            }
            if (moveSlower && !moveFaster) {
                walkingSpeed /= 10f;
            }

            if (keyUp && keyRight && !keyLeft && !keyDown) {
                float angle = rotation.y + 45;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyUp && keyLeft && !keyRight && !keyDown) {
                float angle = rotation.y - 45;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyUp && !keyLeft && !keyRight && !keyDown) {
                float angle = rotation.y;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && keyLeft && !keyRight && !keyUp) {
                float angle = rotation.y - 135;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && keyRight && !keyLeft && !keyUp) {
                float angle = rotation.y + 135;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && !keyUp && !keyLeft && !keyRight) {
                float angle = rotation.y;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = -(walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyLeft && !keyRight && !keyUp && !keyDown) {
                float angle = rotation.y - 90;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyRight && !keyLeft && !keyUp && !keyDown) {
                float angle = rotation.y + 90;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (flyUp && !flyDown) {
                double newPositionY = (walkingSpeed * 0.0002) * delta;
                position.y -= newPositionY;
            }
            if (flyDown && !flyUp) {
                double newPositionY = (walkingSpeed * 0.0002) * delta;
                position.y += newPositionY;
            }
            if (moveFaster && !moveSlower) {
                walkingSpeed /= 4f;
            }
            if (moveSlower && !moveFaster) {
                walkingSpeed *= 10f;
            }
            while (Mouse.next()) {
                if (Mouse.isButtonDown(0)) {
                    Mouse.setGrabbed(true);
                }
                if (Mouse.isButtonDown(1)) {
                    Mouse.setGrabbed(false);
                }
            }
            while (Keyboard.next()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                    position = new Vector3f(0, 0, 0);
                    rotation = new Vector3f(0, 0, 0);
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
                    mouseSpeed += 1;
                    System.out.println("Mouse speed changed to " + mouseSpeed + ".");
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
                    if (mouseSpeed - 1 > 0) {
                        mouseSpeed -= 1;
                        System.out.println("Mouse speed changed to " + mouseSpeed + ".");
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
                    System.out.println("Walking speed changed to " + walkingSpeed + ".");
                    walkingSpeed += 1;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
                    System.out.println("Walking speed changed to " + walkingSpeed + ".");
                    walkingSpeed -= 1;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {
                    try {
                        Display.setFullscreen(!Display.isFullscreen());
                        if (!Display.isFullscreen()) {
                            Display.setResizable(resizable);
                            Display.setDisplayMode(new DisplayMode(800, 600));
                            glViewport(0, 0, Display.getWidth(), Display.getHeight());
                            glMatrixMode(GL_PROJECTION);
                            glLoadIdentity();
                            gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
                            glMatrixMode(GL_MODELVIEW);
                            glLoadIdentity();
                        } else {
                            glViewport(0, 0, Display.getWidth(), Display.getHeight());
                            glMatrixMode(GL_PROJECTION);
                            glLoadIdentity();
                            gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
                            glMatrixMode(GL_MODELVIEW);
                            glLoadIdentity();
                        }
                    } catch (LWJGLException ex) {
                        Logger.getLogger(MineFront.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                    if (!Mouse.isGrabbed() || Display.isFullscreen()) {
                        running = false;
                    } else {
                        Mouse.setGrabbed(false);
                    }
                }
            }
            if (resizable) {
                if (Display.wasResized()) {
                    glViewport(0, 0, Display.getWidth(), Display.getHeight());
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
                    glMatrixMode(GL_MODELVIEW);
                    glLoadIdentity();
                }
            }
            if (printFPS) {
                updateFPS();
            }
            Display.update();
            if (vsync) {
                Display.sync(60);
            }
            if (Display.isCloseRequested()) {
                running = false;
            }
        }
        glDeleteTextures(floorTexture);
        glDeleteLists(floorDisplayList, 1);
        glDeleteLists(ceilingDisplayList, 1);
        glDeleteLists(wallDisplayList, 1);
        glDeleteLists(objectDisplayList, 1);
        Display.destroy();
        System.exit(0);
    }
}