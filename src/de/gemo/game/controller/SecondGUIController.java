package de.gemo.game.controller;

import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.core.Engine;
import de.gemo.engine.entity.EntityVertex;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.manager.GUIManager;
import de.gemo.engine.units.Vector;

public class SecondGUIController extends GUIManager {

    private VertexManager vertexManager;
    private EntityVertex selectedVertex = null;
    private MyGUIController controller;

    public SecondGUIController(String name, Hitbox hitbox, Vector mouseVector, int z) {
        super(name, hitbox, mouseVector, z);
    }

    @Override
    protected void initManager() {
        controller = (MyGUIController) Engine.INSTANCE.getGUIManager("GUI");
        // ADD VERTEXMANAGER
        vertexManager = new VertexManager(this, controller);
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
        if (event.isDoubleClick()) {
            if (!this.hasFocusedElement()) {
                this.vertexManager.addVertex(event.getX(), event.getY());
            }
        } else {
            controller.getBtn_removeVertex().setVisible(this.hasFocusedElement());
            if (this.hasFocusedElement()) {
                controller.getLbl_position().setLabel("Position: " + getFocusedElement().getXOnScreen() + " / " + getFocusedElement().getYOnScreen());
            } else {
                controller.getLbl_position().setLabel("Position: ___ / ___");
            }
        }
    }

    @Override
    public void onMouseRelease(MouseReleaseEvent event) {
        controller.getBtn_removeVertex().setVisible(this.hasFocusedElement());
        if (this.hasFocusedElement()) {
            controller.getLbl_position().setLabel("Position: " + getFocusedElement().getXOnScreen() + " / " + getFocusedElement().getYOnScreen());
        } else {
            controller.getLbl_position().setLabel("Position: ___ / ___");
        }
    }

    public EntityVertex getSelectedVertex() {
        return selectedVertex;
    }

    public void setSelectedVertex(EntityVertex selectedVertex) {
        this.selectedVertex = selectedVertex;
        if (selectedVertex == null) {
            this.controller.getBtn_removeVertex().setVisible(false);
            this.controller.getLbl_position().setLabel("Position: ___ / ___");
            this.unfocusElement();

        } else {
            this.controller.getBtn_removeVertex().setVisible(true);
            this.controller.getLbl_position().setLabel("Position: " + selectedVertex.getXOnScreen() + " / " + selectedVertex.getYOnScreen());
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
