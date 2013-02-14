package de.gemo.engine.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.gemo.engine.collision.CollisionHelper;
import de.gemo.engine.collision.Hitbox;
import de.gemo.engine.entity.Entity;
import de.gemo.engine.events.keyboard.KeyEvent;
import de.gemo.engine.events.mouse.MouseClickEvent;
import de.gemo.engine.events.mouse.MouseDragEvent;
import de.gemo.engine.events.mouse.MouseMoveEvent;
import de.gemo.engine.events.mouse.MouseReleaseEvent;
import de.gemo.engine.gui.GUIElement;
import de.gemo.engine.gui.GUIElementStatus;
import de.gemo.engine.inputmanager.MouseManager;
import de.gemo.engine.interfaces.input.IKeyAdapter;
import de.gemo.engine.interfaces.input.IKeyController;
import de.gemo.engine.interfaces.input.IMouseAdapter;
import de.gemo.engine.interfaces.input.IMouseController;
import de.gemo.engine.units.Vector;

public abstract class GUIController implements IKeyAdapter, IMouseAdapter, IKeyController, IMouseController, Comparable<GUIController> {

    private final int ID;
    private final int z;
    private final String name;
    private List<GUIElement> sortedList = new ArrayList<GUIElement>();
    private HashMap<Integer, GUIElement> allElements, visibleElements, invisibleElements;
    private final Hitbox hitbox;
    protected final Vector mouseVector;

    protected GUIElement hoveredElement = null, focusedElement = null;

    public GUIController(String name, Hitbox hitbox, Vector mouseVector, int z) {
        this.ID = Entity.getNextFreeID();
        this.z = z;
        this.name = name;
        this.hitbox = hitbox;
        this.mouseVector = mouseVector;
        this.allElements = new HashMap<Integer, GUIElement>();
        this.visibleElements = new HashMap<Integer, GUIElement>();
        this.invisibleElements = new HashMap<Integer, GUIElement>();
    }

    public final void initializeController() {
        this.initController();
        this.loadTextures();
        this.initGUI();
    }

    public final void clear() {
        this.allElements.clear();
        this.visibleElements.clear();
        this.invisibleElements.clear();
    }

    public final void add(GUIElement element) {
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
        this.sortedList.toArray();
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

    public final boolean hasHoveredElement() {
        return hoveredElement != null;
    }

    public final GUIElement getFocusedElement() {
        return focusedElement;
    }

    public final boolean hasFocusedElement() {
        return focusedElement != null;
    }

    public final void focusElement(GUIElement elementToFocus) {
        if (elementToFocus == null) {
            this.unfocusElement();
        } else {
            if (elementToFocus != this.focusedElement) {
                this.unfocusElement();
            }
            this.focusedElement = elementToFocus;
            this.focusedElement.setFocused(true);
            this.focusedElement.fireFocusGainedEvent();
            System.out.println("focus");
        }
    }

    public final void unfocusElement() {
        if (this.focusedElement != null) {
            this.focusedElement.setStatus(GUIElementStatus.NONE);
            this.focusedElement.setFocused(false);
            this.focusedElement.fireFocusLostEvent();
            System.out.println("unfocus");
        }
        this.focusedElement = null;
    }

    public final boolean isColliding() {
        return CollisionHelper.isVectorInHitbox(MouseManager.INSTANCE.getTempMouseVector(), this.hitbox) && CollisionHelper.isVectorInHitbox(this.mouseVector, this.hitbox);
    }

    public final Hitbox getHitbox() {
        return hitbox;
    }

    public final Collection<GUIElement> getVisibleElements() {
        return this.visibleElements.values();
    }

    public final Collection<GUIElement> getAllElements() {
        return this.allElements.values();
    }

    // //////////////////////////////////////////
    //
    // OPTIMIZATION-METHODS
    //
    // //////////////////////////////////////////

    protected void initController() {
    }

    protected void loadTextures() {
    }

    protected void initGUI() {
    }

    public void doTick(float delta) {
    }

    public final void updateCollisions() {
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
                    this.focusedElement.setFocused(false);
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
        if (this.focusedElement != null && this.focusedElement.isAutoLooseFocus()) {
            this.focusedElement.setFocused(false);
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
    public final boolean handleMouseClick(MouseClickEvent event) {
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
        this.onMouseClick(event);
        return true;
    }

    @Override
    public final boolean handleMouseMove(MouseMoveEvent event) {
        if (this.hoveredElement != null && this.hoveredElement.isVectorInClickbox(this.mouseVector)) {
            this.hoveredElement.fireMouseEvent(new MouseMoveEvent((int) (event.getX() - this.hoveredElement.getX() + (this.hoveredElement.getWidth() / 2)), (int) (event.getY() - this.hoveredElement.getY() + (this.hoveredElement.getHeight() / 2)), event.getDifX(), event.getDifY()));
        }
        this.onMouseMove(event);
        return true;
    }

    @Override
    public final boolean handleMouseDrag(MouseDragEvent event) {
        if (this.focusedElement != null && this.focusedElement.isVectorInClickbox(this.mouseVector)) {
            this.focusedElement.fireMouseEvent(new MouseDragEvent((int) (event.getX() - this.focusedElement.getX() + (this.focusedElement.getWidth() / 2)), (int) (event.getY() - this.focusedElement.getY() + (this.focusedElement.getHeight() / 2)), event.getDifX(), event.getDifY(), event.getButton()));
        }
        this.onMouseDrag(event);
        return true;
    }

    @Override
    public final boolean handleMouseRelease(MouseReleaseEvent event) {
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
        this.onMouseRelease(event);
        return true;
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
    }

    @Override
    public void onMouseDrag(MouseDragEvent event) {
    }

    @Override
    public void onMouseRelease(MouseReleaseEvent event) {
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
            if (!this.focusedElement.handleKeyHold(event)) {
                return true;
            }
        }
        this.onKeyHold(event);
        return true;
    }

    @Override
    public final boolean handleKeyReleased(KeyEvent event) {
        if (this.focusedElement != null && !this.focusedElement.isAutoLooseFocus()) {
            if (!this.focusedElement.handleKeyReleased(event)) {
                return true;
            }
        }
        this.onKeyReleased(event);
        return true;
    }

    @Override
    public void onKeyHold(KeyEvent event) {
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
    }

    @Override
    public void onKeyReleased(KeyEvent event) {
    }

    @Override
    public int compareTo(GUIController o) {
        return (int) (this.z - o.z);
    }

}
