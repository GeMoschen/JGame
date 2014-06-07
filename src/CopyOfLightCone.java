import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilOp;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

public class CopyOfLightCone {
    public Vector2f location;
    public float red;
    public float green;
    public float blue;
    public float intensity = 1;
    public float angle = 0f, momentum = 0f;

    public CopyOfLightCone(Vector2f location, float red, float green, float blue) {
        this.location = location;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.momentum = (float) Math.random() * 1.5f;
        if (Math.random() < 0.5) {
            this.momentum = -this.momentum;
        }
    }

    public void render(List<Block> blocks, Shader coneShader, Shader ambientShader, int width, int height) {

        // enable stencil
        glColorMask(false, false, false, false);
        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

        // render ShadowFins
        for (Block block : blocks) {
            Vector2f[] vertices = block.getVertices();
            for (int i = 0; i < vertices.length; i++) {
                Vector2f currentVertex = vertices[i];
                Vector2f nextVertex = vertices[(i + 1) % vertices.length];
                Vector2f edge = Vector2f.sub(nextVertex, currentVertex, null);
                Vector2f normal = new Vector2f(edge.getY(), -edge.getX());
                Vector2f lightToCurrent = Vector2f.sub(currentVertex, this.location, null);
                if (Vector2f.dot(normal, lightToCurrent) > 0) {
                    Vector2f point1 = Vector2f.add(currentVertex, (Vector2f) Vector2f.sub(currentVertex, this.location, null).scale(width), null);
                    Vector2f point2 = Vector2f.add(nextVertex, (Vector2f) Vector2f.sub(nextVertex, this.location, null).scale(width), null);
                    new ShadowFin(currentVertex, point1, point2, nextVertex).render(0, 0, 0, 1f);
                }
            }
        }

        // enable stencil
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glStencilFunc(GL_EQUAL, 0, 1);
        glColorMask(true, true, true, true);

        // bind shader
        coneShader.bind();
        glUniform2f(glGetUniformLocation(coneShader.getID(), "lightLocation"), this.location.getX(), height - this.location.getY());
        glUniform3f(glGetUniformLocation(coneShader.getID(), "lightColorOne"), 0.1f, 0.4f, 0.0f);
        glUniform3f(glGetUniformLocation(coneShader.getID(), "lightColorTwo"), 0.05f, 0.2f, 0.0f);
        // glUniform1f(glGetUniformLocation(coneShader.getID(), "intensity"),
        // this.intensity);

        // enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        // render cone
        glPushMatrix();
        {
            glTranslatef(this.location.x, this.location.y, 0f);
            glRotatef(this.angle, 0, 0, 1);
            glBegin(GL_POLYGON);
            {
                glVertex2f(0, 0);

                glVertex2f(-100, -243);
                glVertex2f(-92, -248);
                glVertex2f(-75, -255);
                glVertex2f(-50, -263);
                glVertex2f(-25, -268);

                glVertex2f(0, -270);

                glVertex2f(+25, -268);
                glVertex2f(+50, -263);
                glVertex2f(+75, -255);
                glVertex2f(+92, -248);
                glVertex2f(+100, -243);
            }
            glEnd();
        }
        glPopMatrix();

        // render outline
        glPushMatrix();
        {
            glTranslatef(this.location.x, this.location.y, 0f);
            glRotatef(this.angle, 0, 0, 1);
            glColor4f(0.2f, 0.5f, 0, 1f);
            glBegin(GL_LINE_LOOP);
            {
                glVertex2f(0, 0);

                glVertex2f(-100, -243);
                glVertex2f(-92, -248);
                glVertex2f(-75, -255);
                glVertex2f(-50, -263);
                glVertex2f(-25, -268);

                glVertex2f(0, -270);

                glVertex2f(+25, -268);
                glVertex2f(+50, -263);
                glVertex2f(+75, -255);
                glVertex2f(+92, -248);
                glVertex2f(+100, -243);
            }
            glEnd();
        }
        glPopMatrix();

        // disable blending, shader and stencil
        glDisable(GL_BLEND);
        coneShader.unbind();
        glClear(GL_STENCIL_BUFFER_BIT);
    }

    public void update() {
        this.angle += this.momentum;
    }
}
