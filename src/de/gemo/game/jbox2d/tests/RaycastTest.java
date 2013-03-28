package de.gemo.game.jbox2d.tests;

import java.util.ArrayList;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.lwjgl.BufferUtils;

import de.gemo.engine.core.Engine;
import de.gemo.game.jbox2d.JBox2D;

import static org.lwjgl.opengl.GL11.*;

public class RaycastTest extends Test {

    private ArrayList<RayCastAnyCallback> castList = new ArrayList<RayCastAnyCallback>();

    private int textureID;

    int width, height;

    public RaycastTest(JBox2D jBox) {
        super(jBox);

        width = Engine.INSTANCE.VIEW_WIDTH;
        height = Engine.INSTANCE.VIEW_HEIGHT;
    }

    @Override
    public void init() {
        this.castList.clear();
        for (int i = 0; i < 360 * 8; i++) {
            castList.add(new RayCastAnyCallback());
        }

        textureID = this.createTexture();
        this.createFBO();
    }

    private int createTexture() {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID); // Bind The Texture
        glTexImage2D(GL_TEXTURE_2D, 0, 4, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(width * height * 4));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        return textureID;
    }

    private void createFBO() {
        this.update(1);
        this.renderToTexture();
    }

    @Override
    public void update(int delta) {
        // RAYCAST
        float angle = 0;
        for (int i = 0; i < this.castList.size(); i++) {
            RayCastAnyCallback raycastCallback = this.castList.get(i);

            Vec2 startPoint = this.jBox.getWorld().getBodyList().getPosition();

            float maxDistance = 300.0F;
            Vec2 d = new Vec2(maxDistance * MathUtils.cos(angle), maxDistance * MathUtils.sin(angle));
            Vec2 endPoint = new Vec2();
            endPoint.set(startPoint);
            endPoint.addLocal(d);
            raycastCallback.init(startPoint, endPoint);
            angle += 360f / this.castList.size();
        }
    }

    private void renderToTexture() {
        this.textureID = this.createTexture();

        glDisable(org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB);
        // glLoadIdentity();
        glEnable(GL_BLEND);

        glViewport(0, 0, width, height);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_COLOR);
        for (int i = 0; i < this.castList.size(); i++) {
            RayCastAnyCallback raycastCallback = this.castList.get(i);
            this.jBox.getWorld().raycast(raycastCallback, raycastCallback.startPoint, raycastCallback.endPoint);
            Vec2 hit = raycastCallback.endPoint;

            glBegin(GL_LINES);
            {
                float alpha = (1f - (Math.abs(MathUtils.distance(raycastCallback.startPoint, hit)) / 300f)) * 0.7f;

                glColor4f(1, 1, 1, 0.7f);
                renderVec2(raycastCallback.startPoint);
                glColor4f(1, 1, 1, alpha);
                renderVec2(raycastCallback.endPoint);
            }
            glEnd();
        }

        // Bind To The Blur Texture
        glBindTexture(GL_TEXTURE_2D, this.textureID);

        // Copy Our ViewPort To The Blur Texture (From 0,0 To 128,128... No Border)
        glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, 0, 0, width, height, 0);

        glClearColor(0.0f, 0.0f, 0f, 1f); // Set The Clear Color To Medium Blue
        // Clear The Screen And Depth Buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        // glViewport(0, 0, width, height); // Set Viewport (0,0 to 640x480)

        // glBindTexture(GL_TEXTURE_2D, textureID);
        // IntBuffer src = BufferUtils.createIntBuffer(width * height * 2);
        // int[] srcArr = new int[(width * height * 2)];
        // int[] dst = new int[(width * height * 2)];
        // glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, src);
        // BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //
        // for (int i = 0; i < srcArr.length; i++) {
        // srcArr[i] = src.get(i);
        // }
        // Body body = this.jBox.getWorld().getBodyList();
        // GaussianFilter filter = new GaussianFilter();
        // filter.filter(srcArr, dst, 200, 200, 25);
        // IntBuffer dstData = BufferUtils.createIntBuffer(width * height * 2);
        // dstData.put(dst);
        // dstData.flip();
        //
        // glBindTexture(GL_TEXTURE_2D, 0);
        //
        // this.textureID = glGenTextures();
        // glBindTexture(GL_TEXTURE_2D, textureID); // Bind The Texture
        // glTexImage2D(GL_TEXTURE_2D, 0, 1, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, dstData);
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public int render() {

        // glTranslatef(200, 100, 0);
        // glScaled(0.5, 0.5, 1);

        // render raycast
        glEnable(GL_BLEND);

        glDisable(org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB);
        glEnable(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, this.textureID);

        glColor4f(1, 1, 1, 1);
        glDisable(GL_CULL_FACE);
        glPushMatrix();
        {
            glBegin(GL_QUADS);
            {
                glTexCoord2d(0, 1);
                glVertex2i(0, 0);

                glTexCoord2d(1, 1);
                glVertex2i(width, 0);

                glTexCoord2d(1, 0);
                glVertex2i(width, height);

                glTexCoord2d(0, 0);
                glVertex2i(0, height);
            }
            glEnd();
        }
        glPopMatrix();

        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        return 0;
    }

    @Override
    public boolean handleKeyPress(int key) {
        return false;
    }

    @Override
    public boolean handleMousePress(int button) {
        this.renderToTexture();
        return false;
    }

    @Override
    public void cleanUp() {
        for (Body body : this.bodies) {
            if (body.getPosition().y > Engine.INSTANCE.VIEW_HEIGHT + 100) {
                this.jBox.removeBody(body);
            }
        }
    }
}
