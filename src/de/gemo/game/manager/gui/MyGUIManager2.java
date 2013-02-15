package de.gemo.game.manager.gui;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.entity.EntityVertex;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.units.Vector;

public class MyGUIManager2 extends GUIManager {

    private VertexManager vertexManager;
    private EntityVertex selectedVertex = null;
    private MyGUIManager1 guiManager;

    public MyGUIManager2(String name, Hitbox hitbox, Vector mouseVector, int z) {
        super(name, hitbox, mouseVector, z);
    }

    @Override
    protected void initManager() {
        guiManager = (MyGUIManager1) Engine.INSTANCE.getGUIManager("GUI");
        // ADD VERTEXMANAGER
        vertexManager = new VertexManager(this, guiManager);
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
        if (event.isDoubleClick()) {
            if (!this.hasFocusedElement()) {
                this.vertexManager.addVertex(event.getX(), event.getY());
            }
        } else {
            guiManager.getBtn_removeVertex().setVisible(this.hasFocusedElement());
            if (this.hasFocusedElement()) {
                guiManager.getLbl_position().setLabel("Position: " + getFocusedElement().getXOnScreen() + " / " + getFocusedElement().getYOnScreen());
            } else {
                guiManager.getLbl_position().setLabel("Position: N/A");
            }
        }
    }

    @Override
    public void onMouseRelease(MouseReleaseEvent event) {
        guiManager.getBtn_removeVertex().setVisible(this.hasFocusedElement());
        if (this.hasFocusedElement()) {
            guiManager.getLbl_position().setLabel("Position: " + getFocusedElement().getXOnScreen() + " / " + getFocusedElement().getYOnScreen());
        } else {
            guiManager.getLbl_position().setLabel("Position: N/A");
        }
    }

    public EntityVertex getSelectedVertex() {
        return selectedVertex;
    }

    public void setSelectedVertex(EntityVertex selectedVertex) {
        this.selectedVertex = selectedVertex;
        if (selectedVertex == null) {
            this.guiManager.getBtn_removeVertex().setVisible(false);
            this.guiManager.getLbl_position().setLabel("Position: N/A");
            this.unfocusElement();

        } else {
            this.guiManager.getBtn_removeVertex().setVisible(true);
            this.guiManager.getLbl_position().setLabel("Position: " + selectedVertex.getXOnScreen() + " / " + selectedVertex.getYOnScreen());
        }
        if (this.focusedElement != null && selectedVertex == null) {
            this.unfocusElement();
        }
    }

    public VertexManager getVertexManager() {
        return vertexManager;
    }

    @Override
    public void render() {
        this.vertexManager.render();
        super.render();
    }

}
