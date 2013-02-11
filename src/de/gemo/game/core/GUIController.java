package de.gemo.game.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.gemo.game.collision.CollisionHelper;
import de.gemo.game.collision.Hitbox;
import de.gemo.game.entity.Entity;
import de.gemo.game.events.keyboard.KeyEvent;
import de.gemo.game.events.mouse.MouseDownEvent;
import de.gemo.game.events.mouse.MouseDragEvent;
import de.gemo.game.events.mouse.MouseMoveEvent;
import de.gemo.game.events.mouse.MouseReleaseEvent;
import de.gemo.game.gui.GUIElement;
import de.gemo.game.gui.GUIElementStatus;
import de.gemo.game.interfaces.IKeyAdapter;
import de.gemo.game.interfaces.IKeyController;
import de.gemo.game.interfaces.IMouseController;
import de.gemo.game.interfaces.Vector;

public abstract class GUIController implements IKeyController, IMouseController, IKeyAdapter {

    private final int ID;
    private final String name;
    private List<GUIElement> sortedList = new ArrayList<GUIElement>();
    private HashMap<Integer, GUIElement> allElements, visibleElements, invisibleElements;
    private final Hitbox hitbox;
    protected final Vector mouseVector;

    protected GUIElement hoveredElement = null, focusedElement = null;

    public GUIController(String name, Hitbox hitbox, Vector mouseVector) {
        this.ID = Entity.getNextFreeID();
        this.name = name;
        this.hitbox = hitbox;
        this.mouseVector = mouseVector;
        this.allElements = new HashMap<Integer, GUIElement>();
        this.visibleElements = new HashMap<Integer, GUIElement>();
        this.invisibleElements = new HashMap<Integer, GUIElement>();
        this.init();
    }

    public final void clear() {
        this.allElements.clear();
        this.visibleElements.clear();
        this.invisibleElements.clear();
    }

    public final void add(GUIElement element) {
        this.allElements.put(element.getEntityID(), element);
        this.addToAll(element);
        this.updateVisibility(element);
    }

    private final void addToAll(GUIElement element) {
        this.allElements.put(element.getEntityID(), element);
    }

    private final void addVisible(GUIElement element) {
        this.visibleElements.put(element.getEntityID(), element);
        this.sortedList = new ArrayList<GUIElement>(this.visibleElements.values());
        Collections.sort(this.sortedList);
    }

    private final void addInvisible(GUIElement element) {
        this.invisibleElements.put(element.getEntityID(), element);
    }

    public final GUIElement getVisible(int entityID) {
        return this.visibleElements.get(entityID);
    }

    public final GUIElement getVisible(GUIElement element) {
        return this.getVisible(element.getEntityID());
    }

    public final GUIElement getInvisible(int entityID) {
        return this.invisibleElements.get(entityID);
    }

    public final GUIElement getInvisible(GUIElement element) {
        return this.getInvisible(element.getEntityID());
    }

    public final boolean has(int entityID) {
        return this.allElements.containsKey(entityID);
    }

    public final boolean has(GUIElement element) {
        return this.has(element.getEntityID());
    }

    public final boolean hasVisible(int entityID) {
        return this.visibleElements.containsKey(entityID);
    }

    public final boolean hasVisible(GUIElement element) {
        return this.hasVisible(element.getEntityID());
    }

    public final boolean hasInvisible(int entityID) {
        return this.invisibleElements.containsKey(entityID);
    }

    public final boolean hasInvisible(GUIElement element) {
        return this.hasInvisible(element.getEntityID());
    }

    private final void partialRemove(GUIElement element) {
        this.visibleElements.remove(element.getEntityID());
        this.invisibleElements.remove(element.getEntityID());
        this.sortedList = new ArrayList<GUIElement>(this.visibleElements.values());
        Collections.sort(this.sortedList);
    }

    public final void remove(GUIElement element) {
        this.partialRemove(element);
        this.allElements.remove(element.getEntityID());
    }

    public void render() {
        for (GUIElement element : this.sortedList) {
            Renderer.render(element);
        }
    }

    public void debugRender() {
        Renderer.renderHitbox(this.getName(), this.hitbox);
        for (GUIElement element : this.visibleElements.values()) {
            Renderer.debugRender(element);
        }
    }

    public final int getID() {
        return ID;
    }

    public final String getName() {
        return name;
    }

    public final GUIElement getHoveredElement() {
        return hoveredElement;
    }

    public GUIElement getFocusedElement() {
        return focusedElement;
    }

    public final boolean isColliding() {
        return CollisionHelper.isVectorInHitbox(this.mouseVector, this.hitbox);
    }

    public final Hitbox getHitbox() {
        return hitbox;
    }

    public Collection<GUIElement> getVisibleElements() {
        return this.visibleElements.values();
    }

    public Collection<GUIElement> getAllElements() {
        return this.allElements.values();
    }

    // //////////////////////////////////////////
    //
    // OPTIMIZATION-METHODS
    //
    // //////////////////////////////////////////

    protected abstract void init();

    public void updateCollisions() {
        boolean isColliding = false;

        // unhover element, if element is outside
        for (GUIElement element : this.sortedList) {
            if (!element.isVectorInClickbox(this.mouseVector) && element.isHovered()) {
                element.setStatus(GUIElementStatus.NONE);
                element.fireHoverEndEvent();
            }
        }

        // unfocus element, if the mouse is outside the hitbox
        if (this.focusedElement != null) {
            if (this.focusedElement.isAutoLooseFocus()) {
                if (!this.focusedElement.isVectorInClickbox(this.mouseVector)) {
                    this.focusedElement.fireFocusLostEvent();
                    this.focusedElement.setStatus(GUIElementStatus.NONE);
                    this.focusedElement = null;
                }
            }
        }

        GUIElement oldHoveringElement = this.hoveredElement;
        GUIElement newHoveringElement = null;

        // get the current hovering element
        GUIElement element;
        for (int index = this.sortedList.size() - 1; index > -1; index--) {
            element = this.sortedList.get(index);
            isColliding = element.isVectorInClickbox(this.mouseVector);
            if (isColliding) {
                newHoveringElement = element;
                break;
            }
        }

        if (oldHoveringElement != null || newHoveringElement != null) {
            // old element != new element
            if (oldHoveringElement != newHoveringElement) {
                if (newHoveringElement != null) {
                    // fire hover begin event
                    newHoveringElement.setStatus(GUIElementStatus.HOVERING);
                    newHoveringElement.fireHoverBeginEvent();
                    this.hoveredElement = newHoveringElement;
                }
                if (oldHoveringElement != null) {
                    // fire hover end event
                    if (oldHoveringElement.isHovered()) {
                        oldHoveringElement.setStatus(GUIElementStatus.NONE);
                        oldHoveringElement.fireHoverEndEvent();
                    }
                    this.hoveredElement = null;
                }
                return;
            } else {
                if (oldHoveringElement != null) {
                    if (oldHoveringElement.getStatus().equals(GUIElementStatus.HOVERING)) {
                        // fire hover event
                        oldHoveringElement.fireHoverEvent();
                        this.hoveredElement = newHoveringElement;
                    }
                }
            }
        }
    }

    public final void updateController() {
        // update visibility of all elements
        for (GUIElement element : this.allElements.values()) {
            this.updateVisibility(element);
        }

        // check collisions
        if (this.isColliding()) {
            this.updateCollisions();
        }

        // tick all elements
        for (GUIElement element : this.allElements.values()) {
            element.doTick();
        }
    }

    private final void updateVisibility(GUIElement element) {
        if (element.isVisible()) {
            if (!element.isCollidingWithClickbox(this.hitbox)) {
                element.setVisible(false);
                this.partialRemove(element);
                this.addInvisible(element);
            } else {
                if (!this.hasVisible(element)) {
                    element.setVisible(true);
                    this.partialRemove(element);
                    this.addVisible(element);
                }
            }
        } else {
            if (!this.hasInvisible(element)) {
                this.partialRemove(element);
                this.addInvisible(element);
            }
        }
    }

    // //////////////////////////////////////////
    //
    // MOUSE-EVENTS
    //
    // //////////////////////////////////////////

    public void onMouseIn() {
    }

    public void onMouseOut() {
        if (this.focusedElement != null) {
            this.focusedElement.fireFocusLostEvent();
            this.focusedElement.setStatus(GUIElementStatus.NONE);
            this.focusedElement = null;
        }
        if (this.hoveredElement != null) {
            this.hoveredElement.setStatus(GUIElementStatus.NONE);
            this.hoveredElement.fireHoverEndEvent();
            this.hoveredElement = null;
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (this.getHoveredElement() != null) {
            if (this.getHoveredElement().isVectorInClickbox(this.mouseVector)) {
                if (this.focusedElement != null && this.focusedElement != this.getHoveredElement()) {
                    this.getHoveredElement().setStatus(GUIElementStatus.ACTIVE);
                    this.getHoveredElement().fireMouseEvent(event);
                    this.focusedElement.setFocused(false);
                    this.focusedElement.fireFocusLostEvent();
                    this.focusedElement = null;
                }
                if (this.focusedElement != this.getHoveredElement()) {
                    this.getHoveredElement().setStatus(GUIElementStatus.ACTIVE);
                    this.getHoveredElement().fireMouseEvent(event);
                    this.focusedElement = this.getHoveredElement();
                    this.focusedElement.setFocused(true);
                    this.focusedElement.fireFocusGainedEvent();
                }
            } else {
                this.getHoveredElement().setStatus(GUIElementStatus.NONE);
            }
        } else {
            if (this.focusedElement != null) {
                this.focusedElement.setFocused(false);
                this.focusedElement.fireFocusLostEvent();
                this.focusedElement = null;
            }
        }
    }

    @Override
    public abstract void onMouseMove(MouseMoveEvent event);

    @Override
    public abstract void onMouseDrag(MouseDragEvent event);

    @Override
    public void onMouseUp(MouseReleaseEvent event) {
        if (this.focusedElement != null) {
            if (this.focusedElement != null && this.focusedElement.isAutoLooseFocus()) {
                this.focusedElement.setFocused(false);
                this.focusedElement.fireFocusLostEvent();
                if (this.focusedElement.isVectorInClickbox(this.mouseVector)) {
                    this.focusedElement.fireMouseEvent(event);
                    this.focusedElement.setStatus(GUIElementStatus.HOVERING);
                    this.hoveredElement = this.focusedElement;
                    this.hoveredElement.fireHoverBeginEvent();
                } else {
                    this.focusedElement.setStatus(GUIElementStatus.NONE);
                }
                this.focusedElement = null;
            }
        }

    }

    // //////////////////////////////////////////
    //
    // KEY-EVENTS
    //
    // //////////////////////////////////////////

    @Override
    public final boolean handleKeyPressed(KeyEvent event) {
        if (this.focusedElement != null && !this.focusedElement.isAutoLooseFocus()) {
            if (this.focusedElement.handleKeyPressed(event)) {
                return true;
            }
        }
        this.onKeyPressed(event);
        return true;
    }

    @Override
    public final boolean handleKeyHold(KeyEvent event) {
        if (this.focusedElement != null && !this.focusedElement.isAutoLooseFocus()) {
            if (this.focusedElement.handleKeyHold(event)) {
                return true;
            }
        }
        this.onKeyHold(event);
        return true;
    }

    @Override
    public final boolean handleKeyReleased(KeyEvent event) {
        if (this.focusedElement != null && !this.focusedElement.isAutoLooseFocus()) {
            if (this.focusedElement.handleKeyReleased(event)) {
                return true;
            }
        }
        this.onKeyReleased(event);
        return true;
    }

    @Override
    public abstract void onKeyHold(KeyEvent event);

    @Override
    public abstract void onKeyPressed(KeyEvent event);

    @Override
    public abstract void onKeyReleased(KeyEvent event);
}
