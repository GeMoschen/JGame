package de.gemo.gameengine.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.gemo.gameengine.events.mouse.MouseClickEvent;
import de.gemo.gameengine.events.mouse.MouseDragEvent;
import de.gemo.gameengine.events.mouse.MouseMoveEvent;
import de.gemo.gameengine.events.mouse.MouseReleaseEvent;
import de.gemo.gameengine.events.mouse.MouseWheelEvent;
import de.gemo.gameengine.gui.GUIElement;

public class GUIManager {
	public static GUIManager INSTANCE = null;

	public static GUIManager getInstance() {
		if (INSTANCE == null) {
			return INSTANCE = new GUIManager();
		} else {
			throw new RuntimeException("ERROR: GUIManager is already created!");
		}
	}

	private final Map<String, GUIElement> elements = new ConcurrentHashMap<String, GUIElement>();
	private GUIElement hoveredElement = null, focusedElement = null;

	// ////////////////////////////////////////
	//
	// RENDERING
	//
	// ////////////////////////////////////////

	public void renderElements() {
		for (GUIElement element : this.elements.values()) {
			if (element.isVisible()) {
				element.addToRenderPipeline();
			}
		}
	}

	// ////////////////////////////////////////
	//
	// ELEMENTS
	//
	// ////////////////////////////////////////

	public boolean hasElement(String name) {
		return this.elements.containsKey(name);
	}

	public boolean hasElement(GUIElement element) {
		return this.elements.containsValue(element);
	}

	public boolean addElement(String name, GUIElement element) {
		if (this.hasElement(name)) {
			return false;
		}
		return (this.elements.put(name, element) == null);
	}

	public GUIElement getElement(String name) {
		return this.elements.get(name);
	}

	public boolean removeElement(GUIElement element) {
		String name = null;
		for (Map.Entry<String, GUIElement> entry : this.elements.entrySet()) {
			if (element.equals(entry.getValue())) {
				name = entry.getKey();
				break;
			}
		}
		if (name != null) {
			if (element == this.hoveredElement) {
				this.hoveredElement = null;
			}
			if (element == this.focusedElement) {
				this.focusedElement = null;
			}
			this.elements.remove(name);
			return true;
		}
		return false;
	}

	public boolean removeElement(String name) {
		if (!this.hasElement(name)) {
			return false;
		}
		if (this.elements.get(name) == this.hoveredElement) {
			this.hoveredElement = null;
		}
		if (this.elements.get(name) == this.focusedElement) {
			this.focusedElement = null;
		}
		return (this.elements.remove(name) != null);
	}

	public void clearElements() {
		this.elements.clear();
	}

	public GUIElement getElementUnderMouse() {
		GUIElement activeElement = null;
		for (GUIElement element : this.elements.values()) {
			if (element.isColliding(MouseManager.INSTANCE.getHitBox())) {
				if (activeElement == null || activeElement.getPosition().getZ() <= element.getPosition().getZ()) {
					activeElement = element;
				}
			}
		}
		return activeElement;
	}

	public GUIElement getHoveredElement() {
		return hoveredElement;
	}

	public GUIElement getFocusedElement() {
		return focusedElement;
	}

	// ////////////////////////////////////////
	//
	// MOUSE-EVENTS
	//
	// ////////////////////////////////////////

	public boolean onMouseWheel(MouseWheelEvent event) {
		GUIElement element = this.getElementUnderMouse();
		if (element != null) {
			if (!element.equals(this.hoveredElement)) {
				if (this.hoveredElement != null) {
					this.hoveredElement.onHoverEnd();
				}
				this.hoveredElement = element;
				element.onHoverBegin();
			}
			if (!element.equals(this.focusedElement)) {
				if (this.focusedElement != null) {
					this.focusedElement.onFocusLost();
				}
				this.focusedElement = element;
				this.focusedElement.onFocusGained();
			}
			this.focusedElement.onMouseWheel(event);
			return true;
		} else if (this.hoveredElement != null) {
			this.hoveredElement.onHoverEnd();
			this.hoveredElement = null;
		}
		return false;
	}

	public boolean onMouseMove(MouseMoveEvent event) {
		GUIElement element = this.getElementUnderMouse();
		if (element != null) {
			int eventX = (int) (event.getX() - element.getPosition().getX());
			int eventY = (int) (event.getY() - element.getPosition().getY());
			if (!element.equals(this.hoveredElement)) {
				if (this.hoveredElement != null) {
					this.hoveredElement.onHoverEnd();
				}
				this.hoveredElement = element;
				element.onHoverBegin();
			}
			element.onHover();
			element.onMouseMove(new MouseMoveEvent(eventX, eventY, event.getDifX(), event.getDifY()));
			return true;
		} else if (this.hoveredElement != null) {
			this.hoveredElement.onHoverEnd();
			this.hoveredElement = null;
		}
		return false;
	}

	public boolean onMouseDown(MouseClickEvent event) {
		GUIElement element = this.getElementUnderMouse();
		if (element != null) {
			if (!element.equals(this.focusedElement)) {
				if (this.focusedElement != null) {
					this.focusedElement.onFocusLost();
				}
				this.focusedElement = element;
				this.focusedElement.onFocusGained();
			}

			int eventX = (int) (event.getX() - element.getPosition().getX());
			int eventY = (int) (event.getY() - element.getPosition().getY());
			element.onMouseDown(new MouseClickEvent(eventX, eventY, event.getButton(), event.isDoubleClick()));
			return true;
		} else if (this.focusedElement != null) {
			this.focusedElement.onFocusLost();
			this.focusedElement = null;
		}
		return false;
	}

	public boolean onMouseUp(MouseReleaseEvent event) {
		GUIElement element = this.getElementUnderMouse();
		if (element != null) {
			if (!element.equals(this.focusedElement)) {
				if (this.focusedElement != null) {
					this.focusedElement.onFocusLost();
				}
				this.focusedElement = element;
				this.focusedElement.onFocusGained();
			}
			int eventX = (int) (event.getX() - element.getPosition().getX());
			int eventY = (int) (event.getY() - element.getPosition().getY());
			element.onMouseUp(new MouseReleaseEvent(eventX, eventY, event.getButton()));
			return true;
		}
		return false;
	}

	public boolean onMouseDrag(MouseDragEvent event) {
		GUIElement element = this.getElementUnderMouse();
		if (element != null) {
			if (!element.equals(this.focusedElement)) {
				if (this.focusedElement != null) {
					this.focusedElement.onFocusLost();
				}
				this.focusedElement = element;
				this.focusedElement.onFocusGained();
			}
			int eventX = (int) (event.getX() - element.getPosition().getX());
			int eventY = (int) (event.getY() - element.getPosition().getY());
			element.onMouseDrag(new MouseDragEvent(eventX, eventY, event.getDifX(), event.getDifY(), event.getButton()));
			return true;
		}
		return false;
	}
}
