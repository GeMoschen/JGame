package de.gemo.gameengine.interfaces.listener;

import de.gemo.gameengine.gui.GUIElement;

public interface FocusListener {

	public void onFocusGained(GUIElement element);

	public void onFocusLost(GUIElement element);

	public void onHoverBegin(GUIElement element);

	public void onHover(GUIElement element);

	public void onHoverEnd(GUIElement element);
}
