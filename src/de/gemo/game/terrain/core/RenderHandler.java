package de.gemo.game.terrain.core;

import java.util.*;

import org.newdawn.slick.*;
import org.newdawn.slick.opengl.*;

import de.gemo.gameengine.core.*;
import de.gemo.gameengine.manager.*;

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

        // RENDER FPS
        glPushMatrix();
        {
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            Color.white.bind();
            TextureImpl.bindNone();
            FontManager.getStandardFont().drawString(20, 20, "FPS: " + GameEngine.INSTANCE.getDebugMonitor().getFPS());
        }
        glPopMatrix();
    }
}
