package de.gemo.game.terrain.core;

import java.util.*;

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
        for (int i = 0; i < this.objects.size(); i++) {
            this.objects.get(i).render();
        }
    }
}
