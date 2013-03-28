package de.gemo.game.skyroads;

import java.awt.Font;
import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jinngine.collision.SAP2;
import jinngine.geometry.Box;
import jinngine.geometry.ConvexHull;
import jinngine.geometry.Sphere;
import jinngine.math.Matrix3;
import jinngine.math.Matrix4;
import jinngine.math.Vector3;
import jinngine.physics.Body;
import jinngine.physics.ContactTrigger;
import jinngine.physics.ContactTrigger.Callback;
import jinngine.physics.DefaultScene;
import jinngine.physics.DisabledDeactivationPolicy;
import jinngine.physics.Scene;
import jinngine.physics.constraint.contact.ContactConstraint;
import jinngine.physics.force.Force;
import jinngine.physics.force.GravityForce;
import jinngine.physics.force.ImpulseForce;
import jinngine.physics.solver.NonsmoothNonlinearConjugateGradient;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.gemo.engine.core.Engine;
import de.gemo.engine.core.debug.StandardDebugMonitor;
import de.gemo.engine.manager.FontManager;
import de.gemo.engine.manager.KeyboardManager;
import de.gemo.game.engine.model.Model;
import static org.lwjgl.opengl.ARBShadowAmbient.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.util.glu.GLU.*;

public class Skyroads extends Engine {

    public static float SCALE = 1f;

    private int delta = 0;
    private Vector3f camPos = new Vector3f(0, 5, -10);
    private Player player;

    private Level level = new Level(5, 200);

    private Scene scene;

    // SHADOWS

    // This represents if the clients computer has the ambient shadow extention
    private static boolean ambientShadowsAvailable;
    // Disable this if your computer doesn't support the FBO extension
    private static final boolean useFBO = true;

    // The amount of polygon offset to use
    private static float factor = 4.0F;

    private static int maxTextureSize;

    private static int shadowWidth = 640;
    private static int shadowHeight = 480;

    private static int frameBuffer;
    private static int renderBuffer;

    private static final FloatBuffer ambientLight = BufferUtils.createFloatBuffer(4);
    private static final FloatBuffer diffuseLight = BufferUtils.createFloatBuffer(4);
    private static final FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
    private static final FloatBuffer tempBuffer = BufferUtils.createFloatBuffer(4);

    private static final Matrix4f textureMatrix = new Matrix4f();

    /** Sets up the OpenGL states. */
    private void setUpOpenGL() {
        int maxRenderbufferSize = glGetInteger(GL_MAX_RENDERBUFFER_SIZE_EXT);

        if (!GLContext.getCapabilities().OpenGL14 && GLContext.getCapabilities().GL_ARB_shadow) {
            System.out.println("Can't create shadows at all. Requires OpenGL 1.4 or the GL_ARB_shadow extension");
            System.exit(0);
        }

        if (GLContext.getCapabilities().GL_ARB_shadow_ambient) {
            ambientShadowsAvailable = true;
        } else {
            System.out.println("GL_ARB_shadow_ambient extension not availible.\n An extra rendering pass will be " + "required.");
        }

        if (GLContext.getCapabilities().OpenGL20 || GLContext.getCapabilities().GL_EXT_framebuffer_object) {
            System.out.println("Higher quality shadows are availible.");
        }

        maxTextureSize = glGetInteger(GL_MAX_TEXTURE_SIZE);

        System.out.println("Maximum texture size: " + maxTextureSize);
        System.out.println("Maximum renderbuffer size: " + maxRenderbufferSize);

        /*
         * Check to see if the maximum texture size is bigger than 2048. Performance drops too much if it much bigger than that.
         */
        if (maxTextureSize > 2048) {
            maxTextureSize = 2048;
            if (maxRenderbufferSize < maxTextureSize) {
                maxTextureSize = maxRenderbufferSize;
            }
        }

        if (useFBO) {
            shadowWidth = maxTextureSize;
            shadowHeight = maxTextureSize;
        }

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glPolygonOffset(factor, 0.0F);

        glShadeModel(GL_SMOOTH);
        glEnable(GL_LIGHTING);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_NORMALIZE);
        glEnable(GL_LIGHT0);
        glEnable(GL_TEXTURE_2D);

        // Setup some texture states
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);

        // If ambient shadows are availible then we can skip a rendering pass.
        if (ambientShadowsAvailable) {
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FAIL_VALUE_ARB, 0.5F);
        }

        glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGeni(GL_Q, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);

        // If we are using a FBO, we need to setup the framebuffer.
        if (useFBO) {
            frameBuffer = glGenFramebuffersEXT();
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBuffer);

            renderBuffer = glGenRenderbuffersEXT();
            glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, renderBuffer);

            glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT32, maxTextureSize, maxTextureSize);

            glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, renderBuffer);

            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);

            int FBOStatus = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
            if (FBOStatus != GL_FRAMEBUFFER_COMPLETE_EXT) {
                System.out.println("Framebuffer error!");
            }

            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        }
        generateShadowMap();
    }

    /** Sets up the FloatBuffers to be used later on. */
    private void setUpBufferValues() {
        ambientLight.put(new float[]{0.2F, 0.2F, 0.2F, 0.5F});
        ambientLight.flip();

        diffuseLight.put(new float[]{0.7F, 0.7F, 0.7F, 1.0F});
        diffuseLight.flip();

        lightPosition.put(new float[]{0F, 300.0F, 100.0F, 1.0F});
        lightPosition.flip();
    }

    /** Generate the shadow map. */
    private void generateShadowMap() {
        float lightToSceneDistance, nearPlane, fieldOfView;
        FloatBuffer lightModelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer lightProjection = BufferUtils.createFloatBuffer(16);
        Matrix4f lightProjectionTemp = new Matrix4f();
        Matrix4f lightModelViewTemp = new Matrix4f();

        float sceneBoundingRadius = 95.0F;

        lightToSceneDistance = (float) Math.sqrt(lightPosition.get(0) * lightPosition.get(0) + lightPosition.get(1) * lightPosition.get(1) + lightPosition.get(2) * lightPosition.get(2));

        nearPlane = lightToSceneDistance - sceneBoundingRadius;

        fieldOfView = (float) Math.toDegrees(2.0F * Math.atan(sceneBoundingRadius / lightToSceneDistance));

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fieldOfView, 1.0F, nearPlane, nearPlane + (2.0F * sceneBoundingRadius));
        glGetFloat(GL_PROJECTION_MATRIX, lightProjection);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(lightPosition.get(0), lightPosition.get(1), lightPosition.get(2), 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
        glGetFloat(GL_MODELVIEW_MATRIX, lightModelView);
        glViewport(0, 0, shadowWidth, shadowHeight);

        if (useFBO) {
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, frameBuffer);
        }

        glClear(GL_DEPTH_BUFFER_BIT);

        // Set rendering states to the minimum required, for speed.
        glShadeModel(GL_FLAT);
        glDisable(GL_LIGHTING);
        glDisable(GL_COLOR_MATERIAL);
        glDisable(GL_NORMALIZE);
        glColorMask(false, false, false, false);

        glEnable(GL_POLYGON_OFFSET_FILL);

        this.renderShadowmap();

        glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, 0, 0, shadowWidth, shadowHeight, 0);

        // Unbind the framebuffer if we are using them.
        if (useFBO) {
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        }

        // Setup the rendering states.
        glShadeModel(GL_SMOOTH);
        glEnable(GL_LIGHTING);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_NORMALIZE);
        glColorMask(true, true, true, true);
        glDisable(GL_POLYGON_OFFSET_FILL);

        lightProjectionTemp.load(lightProjection);
        lightModelViewTemp.load(lightModelView);
        lightProjection.flip();
        lightModelView.flip();

        Matrix4f tempMatrix = new Matrix4f();
        tempMatrix.setIdentity();
        tempMatrix.translate(new Vector3f(0.5F, 0.5F, 0.5F));
        tempMatrix.scale(new Vector3f(0.5F, 0.5F, 0.5F));
        Matrix4f.mul(tempMatrix, lightProjectionTemp, textureMatrix);
        Matrix4f.mul(textureMatrix, lightModelViewTemp, tempMatrix);
        Matrix4f.transpose(tempMatrix, textureMatrix);
    }

    // END SHADOWS

    public Skyroads() {
        super("Physics2D", 800, 600, false);
    }

    private final void drawLoadingText(String topic, String text, int percent) {
        glPushMatrix();
        {
            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
            glDisable(org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            TrueTypeFont font = FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 26);

            // draw toptex
            int x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(topic) / 2f);
            int y = (int) (this.VIEW_HEIGHT / 2f - font.getHeight(topic) / 2f);
            font.drawString(x, y, topic, Color.red);
            y += font.getHeight(topic) + 10;

            // draw subtext
            font = FontManager.getFont(FontManager.DEFAULT, Font.BOLD, 20);
            x = (int) (this.VIEW_WIDTH / 2f - font.getWidth(text) / 2f);
            font.drawString(x, y, text, Color.gray);

            int width = 200;
            int height = 40;
            x = (int) (this.VIEW_WIDTH / 2f - width / 2f);
            y = this.VIEW_HEIGHT - 100;
            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);

            float succeeded = percent * 2;

            // DRAW SUCCEEDED PERCENT
            glBegin(GL_QUADS);
            Color.red.bind();
            glVertex3f(x, y, 0);
            Color.green.bind();
            glVertex3f(x + succeeded, y, 0);
            Color.green.bind();
            glVertex3f(x + succeeded, y + height, 0);
            Color.red.bind();
            glVertex3f(x, y + height, 0);
            glEnd();

            // DRAW OUTLINE
            glLineWidth(3f);
            glBegin(GL_LINE_LOOP);
            Color.white.bind();
            glVertex3f(x, y, 0);
            glVertex3f(x + width, y, 0);
            glVertex3f(x + width, y + height, 0);
            glVertex3f(x, y + height, 0);
            glEnd();

            glEnable(org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB);

            // draw line
            Display.update();
        }
        glPopMatrix();
    }

    @Override
    protected void createGUI() {
        // start jinngine
        scene = new DefaultScene(new SAP2(), new NonsmoothNonlinearConjugateGradient(44), new DisabledDeactivationPolicy());
        scene.setTimestep(0.1);

        try {
            Model model = Model.fromFile(new File("models/cube.obj"));
            Model lineModel = Model.fromFile(new File("models/cube_line.obj"));
            for (int i = 0; i < 200; i++) {
                if (i < 60 | i > 61) {
                    this.level.setCube(0, i, new Cube(model, lineModel, this.scene, 0, i));
                }

                // if ((i > 65 && i < 75) || (i > 140 && i < 165) || i < 20) {
                // this.level.setCube(2, i, new Cube(model, lineModel));
                // }
                // if (i < 23) {
                // this.level.setCube(1, i, new Cube(model, lineModel));
                // }
                // if (i < 37) {
                // this.level.setCube(3, i, new Cube(model, lineModel));
                // }
                // if ((i > 75 && i < 138) || i < 45) {
                // this.level.setCube(4, i, new Cube(model, lineModel));
                // }
            }

            model = Model.fromFile(new File("models/ship_01.obj"));
            player = new Player(0, 0, 0, camPos, model, level);
        } catch (Exception e) {
            e.printStackTrace();
            Engine.close(true);
        }

        this.setUpBufferValues();
        this.setUpOpenGL();

        this.initPhysics();
    }

    public static FloatBuffer asFlippedFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }

    @Override
    protected void createManager() {
        this.setDebugMonitor(new StandardDebugMonitor());
    }

    int cooldown = 0;
    Force gravity;

    @Override
    protected void updateGame(int delta) {

        if (cooldown > 0) {
            cooldown--;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && cooldown < 1) {
            ImpulseForce force = new ImpulseForce(this.shape.getReferenceBody(), new Vector3(0, 0, 0), new Vector3(0, 1, 0), 30);
            this.scene.addForce(force);
            cooldown = 18;
        }

        float maxSpeedX = 5.5f;
        if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            if (this.shape.getReferenceBody().getVelocity().x < maxSpeedX) {
                ImpulseForce force = new ImpulseForce(this.shape.getReferenceBody(), new Vector3(0, 0, 0), new Vector3(1, 0, 0), 1);
                this.scene.addForce(force);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
            if (this.shape.getReferenceBody().getVelocity().x > -maxSpeedX) {
                ImpulseForce force = new ImpulseForce(this.shape.getReferenceBody(), new Vector3(0, 0, 0), new Vector3(-1, 0, 0), 1);
                this.scene.addForce(force);
            }
        }

        float maxSpeedZ = 50f;
        if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
            if (this.shape.getReferenceBody().getVelocity().z > -maxSpeedZ) {
                ImpulseForce force = new ImpulseForce(this.shape.getReferenceBody(), new Vector3(0, 0, 0), new Vector3(0, 0, -1), 1);
                this.scene.addForce(force);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
            if (this.shape.getReferenceBody().getVelocity().z < maxSpeedZ) {
                ImpulseForce force = new ImpulseForce(this.shape.getReferenceBody(), new Vector3(0, 0, 0), new Vector3(0, 0, 1), 1);
                this.scene.addForce(force);
            }
        }

        // System.out.println("----");
        float maxSpeedY = 9f;
        if (this.shape.getReferenceBody().getVelocity().y > maxSpeedY) {
            this.shape.getReferenceBody().setVelocity(this.shape.getReferenceBody().getVelocity().x, maxSpeedY, this.shape.getReferenceBody().getVelocity().z);
        }

        this.shape.getReferenceBody().setOrientation(new Matrix3());
        this.scene.tick();
        this.shape.getReferenceBody().setOrientation(new Matrix3());
        this.delta = delta;

        boolean jump = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_SPACE);
        boolean left = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_COMMA);
        boolean right = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_PERIOD);
        boolean up = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_A);
        boolean down = KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_Y);

        // reset position
        if (KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_R)) {
            player.setVelocity(0, 0, 0);
            this.shape.getReferenceBody().setVelocity(0, 0, 0);
            this.shape.getReferenceBody().setPosition(0, 5, 0);
            return;
        }

        // handle playerinput
        // player.handleInput(jump, left, right, up, down);

        // // reset position
        if (KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_UP)) {
            player.addY(0.01f);
        }
        if (KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_DOWN)) {
            player.addY(-0.01f);
        }

        if (KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_I))
            this.shape.getReferenceBody().setPosition(this.shape.getReferenceBody().getPosition().x, this.shape.getReferenceBody().getPosition().y - 0.25f, this.shape.getReferenceBody().getPosition().z);
        else if (KeyboardManager.INSTANCE.isKeyDown(Keyboard.KEY_L))
            this.shape.getReferenceBody().setPosition(this.shape.getReferenceBody().getPosition().x, this.shape.getReferenceBody().getPosition().y + 0.25f, this.shape.getReferenceBody().getPosition().z);

        // player.update(delta);
    }

    private void renderShadowmap() {
        glPushMatrix();
        {
            glRotatef(1f * 500 * -this.player.getVelocityX(), 0, 0, 1);
            glTranslatef(camPos.x, camPos.y, camPos.z);
            // glTranslatef(camPos.x, -camPos.z - 25, -40);
            // glRotatef(90, 1, 0, 0);
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LEQUAL);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
            glPushMatrix();
            {
                // glEnable(GL_BLEND);
                // glEnable(GL_TEXTURE_2D);
                player.render();
                // glDisable(GL_BLEND);
                // glDisable(GL_TEXTURE_2D);
            }
            glPopMatrix();
        }
        glPopMatrix();
    }

    private void renderObjects(boolean world) {

        glPushMatrix();
        {

            this.player.setPosition((float) this.shape.getReferenceBody().getPosition().x, (float) this.shape.getReferenceBody().getPosition().y, (float) (float) this.shape.getReferenceBody().getPosition().z);
            this.player.setVelocity((float) this.shape.getReferenceBody().getVelocity().x, (float) this.shape.getReferenceBody().getVelocity().y, (float) this.shape.getReferenceBody().getVelocity().z);
            // glRotatef(-this.player.getVelocityX(), 0, 0, 1);

            glTranslated(-this.shape.getReferenceBody().getPosition().x, -this.shape.getReferenceBody().getPosition().y - 5, -this.shape.getReferenceBody().getPosition().z - 12);

            glPushMatrix();
            {
                // glTranslatef(camPos.x, -camPos.z - 25, -40);
                glEnable(GL_LIGHT0);
                glEnable(GL_LIGHTING);

                glClearDepth(1.0f);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_CULL_FACE);
                glCullFace(GL_BACK);

                glPushMatrix();
                {
                    if (world) {
                        this.level.render();
                    }
                    player.render();
                }
                glPopMatrix();

                glPushMatrix();
                {
                    double[] arr = M42D(shape.getTransform());
                    DoubleBuffer buffer = BufferUtils.createDoubleBuffer(arr.length);
                    buffer.put(arr);
                    buffer.flip();
                    glMultMatrix(buffer);
                    glColor4f(1, 1, 1, 1);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    Iterator<Vector3[]> i = shape.getFaces();
                    while (i.hasNext()) {
                        glBegin(GL_POLYGON);
                        Vector3[] face = i.next();

                        // compute normal
                        Vector3 n = face[1].sub(face[0]).cross(face[2].sub(face[1])).normalize();

                        for (Vector3 v : face) {
                            glNormal3d(n.x, n.y, n.z);
                            glVertex3d(v.x, v.y, v.z);
                            glTexCoord2f(0.0f, 1.0f);
                        }
                        glEnd();
                    }
                }
                glPopMatrix();

            }
            glPopMatrix();
        }
        glPopMatrix();

    }

    public static double[] M42D(Matrix4 matrix) {
        return new double[]{matrix.a11, matrix.a21, matrix.a31, matrix.a41, matrix.a12, matrix.a22, matrix.a32, matrix.a42, matrix.a13, matrix.a23, matrix.a33, matrix.a43, matrix.a14, matrix.a24, matrix.a34, matrix.a44};
    }

    @Override
    protected void renderGame3D() {
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);

        if (!ambientShadowsAvailable) {
            FloatBuffer lowAmbient = BufferUtils.createFloatBuffer(4);
            lowAmbient.put(new float[]{0.1F, 0.1F, 0.1F, 1.0F});
            lowAmbient.flip();

            FloatBuffer lowDiffuse = BufferUtils.createFloatBuffer(4);
            lowDiffuse.put(new float[]{0.35F, 0.35F, 0.35F, 1.0F});
            lowDiffuse.flip();

            glLight(GL_LIGHT0, GL_AMBIENT, lowAmbient);
            glLight(GL_LIGHT0, GL_DIFFUSE, lowDiffuse);

            renderObjects(true);

            glAlphaFunc(GL_GREATER, 0.9F);
            glEnable(GL_ALPHA_TEST);
        }

        glLight(GL_LIGHT0, GL_AMBIENT, ambientLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, diffuseLight);

        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);

        glEnable(GL_TEXTURE_GEN_S);
        glEnable(GL_TEXTURE_GEN_T);
        glEnable(GL_TEXTURE_GEN_R);
        glEnable(GL_TEXTURE_GEN_Q);

        tempBuffer.put(0, textureMatrix.m00);
        tempBuffer.put(1, textureMatrix.m01);
        tempBuffer.put(2, textureMatrix.m02);
        tempBuffer.put(3, textureMatrix.m03);

        glTexGen(GL_S, GL_EYE_PLANE, tempBuffer);

        tempBuffer.put(0, textureMatrix.m10);
        tempBuffer.put(1, textureMatrix.m11);
        tempBuffer.put(2, textureMatrix.m12);
        tempBuffer.put(3, textureMatrix.m13);

        glTexGen(GL_T, GL_EYE_PLANE, tempBuffer);

        tempBuffer.put(0, textureMatrix.m20);
        tempBuffer.put(1, textureMatrix.m21);
        tempBuffer.put(2, textureMatrix.m22);
        tempBuffer.put(3, textureMatrix.m23);

        glTexGen(GL_R, GL_EYE_PLANE, tempBuffer);

        tempBuffer.put(0, textureMatrix.m30);
        tempBuffer.put(1, textureMatrix.m31);
        tempBuffer.put(2, textureMatrix.m32);
        tempBuffer.put(3, textureMatrix.m33);

        glTexGen(GL_Q, GL_EYE_PLANE, tempBuffer);

        renderObjects(true);

        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_TEXTURE_GEN_S);
        glDisable(GL_TEXTURE_GEN_T);
        glDisable(GL_TEXTURE_GEN_R);
        glDisable(GL_TEXTURE_GEN_Q);

        renderObjects(false);
    }

    public void initPhysics() {
        // create a box
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();

        for (Vector3f vector : this.player.getModel().vertices) {
            vertices.add(new Vector3(-vector.x, vector.y, -vector.z));
        }

        final ConvexHull boxgeometry = new ConvexHull(vertices);
        final Body box = new Body("box", boxgeometry);
        box.setPosition(new Vector3(0, 5, 0));
        // add all to scene
        scene.addBody(box);

        // // put gravity on box
        gravity = new GravityForce(box, new Vector3(0, -1.2, 0));
        scene.addForce(gravity);

        // create a trigger to detect contact forces with some threshold
        // scene.addTrigger(new ContactTrigger(box, 0.3, new Callback() {
        // @Override
        // public void contactAboveThreshold(Body interactingBody, ContactConstraint constraint) {
        // System.out.println("In contact with " + interactingBody);
        // }
        // @Override
        // public void contactBelowThreshold(Body interactingBody, ContactConstraint constraint) {
        // System.out.println("No longer in contact with " + interactingBody);
        // }
        // }));

        vertices = new ArrayList<Vector3>();

        for (Vector3f vector : this.player.getModel().vertices) {
            vertices.add(new Vector3(-vector.x, vector.y, -vector.z));
        }

        final ConvexHull hull = new ConvexHull(vertices);

        shape = new DrawShape() {
            @Override
            public Iterator<Vector3[]> getFaces() {
                return hull.getFaces();
            }
            @Override
            public Matrix4 getTransform() {
                return boxgeometry.getTransform();
            }
            @Override
            public Body getReferenceBody() {
                return boxgeometry.getBody();
            }
        };

    }

    private DrawShape shape;

    private interface DrawShape {
        public Iterator<Vector3[]> getFaces();
        public Matrix4 getTransform();
        public Body getReferenceBody();
    }
}
