package de.gemo.game.terrain.handler;

import java.util.*;

import de.gemo.game.terrain.entities.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderHandler {
    private List<IRenderObject> objects = new ArrayList<IRenderObject>();

    public void add(IRenderObject object) {
        this.objects.add(object);
    }

    public void remove(IRenderObject object) {
        for (int i = 0; i < this.objects.size(); i++) {
            if (this.objects.get(i) == object) {
                this.objects.remove(i);
                return;
            }
        }
    }

    public void renderAll() {
        glEnable(GL_DEPTH_TEST);
        for (int i = 0; i < this.objects.size(); i++) {
            glPushMatrix();
            {
                this.objects.get(i).render();
            }
            glPopMatrix();
        }
    }
}
