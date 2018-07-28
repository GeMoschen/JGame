package de.gemo.game.terrain.handler;

import de.gemo.game.terrain.entities.EntityWeapon;
import de.gemo.game.terrain.entities.IRenderObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.opengl.GL11.*;

public class RenderHandler {

    private static RenderHandler handler;

    private List<IRenderObject> objects = new ArrayList<IRenderObject>();

    public static EntityWeapon CURRENT_BULLET = null;

    public RenderHandler() {
        handler = this;
    }

    public static void addObject(IRenderObject object) {
        handler.add(object);
        if (object instanceof EntityWeapon && ((EntityWeapon) object).cameraFollows() && CURRENT_BULLET == null) {
            CURRENT_BULLET = (EntityWeapon) object;
        }
    }

    public static void removeObject(IRenderObject object) {
        handler.remove(object);
        if (object instanceof EntityWeapon && ((EntityWeapon) object).cameraFollows()) {
            final Timer timer = new Timer(true);
            final TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    CURRENT_BULLET = null;
                }
            };
            timer.schedule(timerTask, 1250);
        }
    }

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
